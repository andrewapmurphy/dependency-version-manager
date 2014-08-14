package com.cyndre.dvm.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.ArtifactUtils;
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
import com.cyndre.dvm.plugin.diff.DependencyEquivalence;
import com.cyndre.dvm.plugin.diff.DiffFilters;
import com.cyndre.dvm.plugin.diff.GitHelper;
import com.cyndre.dvm.plugin.diff.ProjectBuilderHelper;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;

@Mojo(name="diff",
requiresDependencyResolution=ResolutionScope.RUNTIME_PLUS_SYSTEM,
defaultPhase=LifecyclePhase.VALIDATE,
requiresProject=true,
threadSafe=true,
requiresOnline=true)
public class DependencyDiffMojo extends AbstractMojo {
	private static final String NOT_PRESENT = "Not present";
	private static final String OUTPUT_FORMAT = "%s\t%s\t\t%s";
	
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
		MapDifference<String, Dependency> diff = simpleDependencyDiff(thisProject.getFile());
		
		output(diff, output, oldBranch, newBranch);
	}
	
	private void output(final MapDifference<String, Dependency> diff, final String outputPath, final String oldBranch, final String newBranch)
	throws MojoExecutionException {
		final String outputStr = String.format(OUTPUT_FORMAT, "", oldBranch, newBranch)
				+ "\n"
				+ toReadableString(diff.entriesDiffering());
		
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
	
	private MapDifference<String, Dependency> simpleDependencyDiff(final File pomFile)
	throws MojoExecutionException {
		final MavenProject oldProject = checkoutAndBuildMavenProject(pomFile, oldBranch);
		final MavenProject newProject = checkoutAndBuildMavenProject(pomFile, newBranch);
		
		final ImmutableMap<String, Dependency> oldDependencies = getFilteredDependencyMap(oldProject);
		final ImmutableMap<String, Dependency> newDependencies = getFilteredDependencyMap(newProject);
		
		final MapDifference<String, Dependency> diff = Maps.difference(
			oldDependencies,
			newDependencies,
			new DependencyEquivalence()
		);
		
		return diff;
	}
	
	private MavenProject checkoutAndBuildMavenProject(final File pomFile, final String branch)
	throws MojoExecutionException {
		final File projectPath = pomFile.getParentFile();

		GitHelper.checkout(branch, projectPath, getLog());
		
		return ProjectBuilderHelper.buildProject(mavenProjectBuilder, pomFile);
	}
	
	private static ImmutableMap<String, Dependency> getFilteredDependencyMap(final MavenProject project) {
		final Collection<Dependency> filtered = Collections2.filter(project.getDependencies(), DiffFilters.IS_NOT_TEST_SCOPE);
		final SortedSet<Dependency> sorted = new TreeSet<Dependency>(new DependencyComparator());
		
		sorted.addAll(filtered);

		return Maps.uniqueIndex(sorted, new Function<Dependency, String>() {
			@Override public String apply(Dependency dependency) {
				return versionlessKey(dependency);
			}
		});
	}
	
	private static final String toReadableString(final Map<String, ValueDifference<Dependency>> diff) {
		final Map<String, String> readableEntries = Maps.transformEntries(diff, new Maps.EntryTransformer<String, ValueDifference<Dependency>, String>() {
			@Override public String transformEntry(String key, ValueDifference<Dependency> value) {				
				final Dependency oldDependency = value.leftValue();
				final String oldVersion = oldDependency != null ? oldDependency.getVersion() : NOT_PRESENT;
				
				final Dependency newDependency = value.rightValue();
				final String newVersion = newDependency != null ? newDependency.getVersion() : NOT_PRESENT;
				
				
				return String.format(OUTPUT_FORMAT, key, oldVersion, newVersion);
			}
		});
		
		return StringUtils.join(readableEntries.values(), "\n");
	}
	
	private static final String versionlessKey(final Dependency dependency) {
		return ArtifactUtils.versionlessKey(dependency.getGroupId(), dependency.getArtifactId());
	}
	
	private static boolean isMultiModule(final MavenProject project) {
		return !project.getModules().isEmpty();
	}
}
