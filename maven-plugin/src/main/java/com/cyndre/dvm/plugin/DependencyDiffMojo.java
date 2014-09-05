package com.cyndre.dvm.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.codehaus.plexus.util.FileUtils;

import com.cyndre.dvm.plugin.diff.DependencyComparator;
import com.cyndre.dvm.plugin.diff.DiffFilters;
import com.cyndre.dvm.plugin.diff.GitHelper;
import com.cyndre.dvm.plugin.diff.Output;
import com.cyndre.dvm.plugin.diff.ProjectBuilderHelper;
import com.cyndre.dvm.plugin.diff.ProjectHasher;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

@Mojo(name="diff",
requiresDependencyResolution=ResolutionScope.RUNTIME_PLUS_SYSTEM,
defaultPhase=LifecyclePhase.VALIDATE,
requiresProject=true,
threadSafe=true,
requiresOnline=true)
public class DependencyDiffMojo extends AbstractMojo {
	
	@Parameter(
		name="oldBranch", property="oldBranch", readonly=true, required=true
	)
	private String oldBranch;

	@Parameter(
		name="newBranch", property="newBranch", readonly=true, required=true
	)
	private String newBranch;
	
	@Parameter(
		name="output", property="output", readonly=true, required=false
	)
	private String output;
	
	@Component
	private ProjectBuilder mavenProjectBuilder;
	
	@Parameter(defaultValue="${project}", readonly=true)
    private MavenProject thisProject;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		MapDifference<String, String> diff = isMultiModuleChild(thisProject)
			? complexDependencyDiff(thisProject.getFile())
			: simpleDependencyDiff(thisProject.getFile());
		
		
		output(diff, output, oldBranch, newBranch);
	}
	
	private void output(final MapDifference<String, String> diff, final String outputPath, final String oldBranch, final String newBranch)
	throws MojoExecutionException {
		final String outputStr = String.format(Output.OUTPUT_FORMAT, "", oldBranch, newBranch)
				+ "\n"
				+ Output.toReadableString(diff.entriesDiffering())
				+ Output.toReadableOnlyLeft(diff.entriesOnlyOnLeft())
				+ Output.toReadableOnlyRight(diff.entriesOnlyOnRight());
		
		getLog().info("Differences in versions:\n" + outputStr);
		
		if (outputPath == null) {
			return;
		}
		
		final File outputFile = new File(outputPath);
		
		if (outputFile.isDirectory() || (outputFile.exists() && !outputFile.canWrite())) {
			throw new MojoExecutionException("Could not write output file: " + outputPath);
		}
		
		try {
			FileUtils.fileWrite(outputFile, outputStr);
		} catch (IOException e) {
			throw new MojoExecutionException("Error writing output file: " + e.getMessage(), e);
		}
	}
	
	private MapDifference<String, String> simpleDependencyDiff(final File pomFile)
	throws MojoExecutionException {
		getLog().debug("Simple diff");
		
		final MavenProject oldProject = checkoutAndBuildMavenProject(pomFile, oldBranch);
		final MavenProject newProject = checkoutAndBuildMavenProject(pomFile, newBranch);
		
		final ImmutableMap<String, String> oldDependencies = ImmutableMap.copyOf(
			Maps.transformValues(
				getFilteredDependencyMap(oldProject), DEPENDENCY_TO_VERSION
			)
		);
		final ImmutableMap<String, String> newDependencies = ImmutableMap.copyOf(
			Maps.transformValues(
				getFilteredDependencyMap(newProject), DEPENDENCY_TO_VERSION
			)
		);	
		
		getLog().debug("Old Dependencies: " + Output.toReadableString(oldDependencies.values()));
		getLog().debug("New Dependencies: " + Output.toReadableString(newDependencies.values()));
		
		final MapDifference<String, String> diff = Maps.difference(
			oldDependencies,
			newDependencies
		);
		
		return diff;
	}
	
	private MapDifference<String, String> complexDependencyDiff(final File pomFile)
	throws MojoExecutionException {
		getLog().debug("Complex diff");
		
		final MavenProject oldProject = checkoutAndBuildMavenProject(pomFile, oldBranch);
		final Collection<Dependency> oldProjectSiblingModules = siblingModulesOfSameVersion(oldProject, oldProject.getDependencies());
		final Map<String, String> oldDependencies = Maps.transformValues(
			getFilteredDependencyMap(oldProject), DEPENDENCY_TO_VERSION
		);
		final ImmutableMap<String, String> oldSiblingModuleHashes = hashSiblingModules(pomFile, oldProjectSiblingModules);
		oldDependencies.putAll(oldSiblingModuleHashes);
		
		final MavenProject newProject = checkoutAndBuildMavenProject(pomFile, newBranch);
		final Collection<Dependency> newProjectSiblingModules = siblingModulesOfSameVersion(newProject, oldProject.getDependencies());
		final Map<String, String> newDependencies = Maps.transformValues(
			getFilteredDependencyMap(newProject), DEPENDENCY_TO_VERSION
		);
		final ImmutableMap<String, String> newSiblingModuleHashes = hashSiblingModules(pomFile, newProjectSiblingModules);
		newDependencies.putAll(newSiblingModuleHashes);
		
		
		getLog().debug("Old Dependencies: " + Output.toReadableString(oldDependencies.values()));
		getLog().debug("New Dependencies: " + Output.toReadableString(newDependencies.values()));
		
		final MapDifference<String, String> diff = Maps.difference(
			oldDependencies,
			newDependencies
		);
		
		return diff;
	}
	
	private ImmutableMap<String, String> hashSiblingModules(final File projectPomFile, final Collection<Dependency> siblings)
	throws MojoExecutionException {
		final File parentProjectDirectory = projectPomFile.getParentFile().getParentFile();
		
		getLog().debug("Parent project path: " + parentProjectDirectory.getAbsolutePath());
		
		final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		
		try {
			for (final Dependency sibling : siblings) {
				final String fullName = Output.versionlessKey(sibling);
				getLog().debug("Hashing sibling " + fullName);
				
				final String hash = hashSiblingModule(parentProjectDirectory, sibling);
				
				builder.put(fullName, hash);
			}
		} catch (IOException e) {
			throw new MojoExecutionException("Error hashing siblings", e);
		}
		
		return builder.build();
	}
	
	private static final String hashSiblingModule(File parentProjectDirectory, final Dependency sibling)
	throws IOException {
		final File siblingProjectPath = Paths.get(parentProjectDirectory.toURI()).resolve(sibling.getArtifactId()).toFile();
		
		return ProjectHasher.hashDirectoryContents(siblingProjectPath);
	}
	
	private Collection<Dependency> siblingModulesOfSameVersion(final MavenProject project, final Collection<Dependency> dependencies) {
		return Collections2.filter(dependencies, new Predicate<Dependency>() {
			@Override
			public boolean apply(Dependency dep) {
				return project.getGroupId().equals(dep.getGroupId())
					&& project.getArtifactId().equals(dep.getArtifactId())
					&& project.getVersion().equals(dep.getVersion())
					&& DiffFilters.IS_NOT_TEST_SCOPE.apply(dep);
			}
		});
	}
	
	private MavenProject checkoutAndBuildMavenProject(final File pomFile, final String branch)
	throws MojoExecutionException {
		final File projectPath = pomFile.getParentFile();

		GitHelper.checkout(branch, projectPath, getLog());
		
		return ProjectBuilderHelper.buildProject(mavenProjectBuilder, thisProject.getProjectBuildingRequest(), pomFile);
	}
	
	private static ImmutableMap<String, Dependency> getFilteredDependencyMap(final MavenProject project) {
		final Collection<Dependency> filtered = Collections2.filter(project.getDependencies(), DiffFilters.IS_NOT_TEST_SCOPE);
		final SortedSet<Dependency> sorted = new TreeSet<Dependency>(new DependencyComparator());
		
		sorted.addAll(filtered);

		return Maps.uniqueIndex(sorted, new Function<Dependency, String>() {
			@Override public String apply(Dependency dependency) {
				return Output.versionlessKey(dependency);
			}
		});
	}
	
	
	private static final Function<Dependency, String> DEPENDENCY_TO_VERSION = new Function<Dependency, String>() {
		@Override
		public String apply(Dependency d) {
			return d.getVersion();
		}
	};
	
	
	private boolean isMultiModuleChild(final MavenProject project) {
		final MavenProject parentProject = project.getParent();
		
		return parentProject != null && project.getGroupId().equals(parentProject.getGroupId())
			&& parentProject.getModules().contains(project.getArtifactId());
	}
}
