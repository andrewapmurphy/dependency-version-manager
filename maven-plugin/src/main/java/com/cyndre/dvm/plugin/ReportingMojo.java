package com.cyndre.dvm.plugin;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.cyndre.dvm.data.Artifact;
import com.cyndre.dvm.data.Project;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Mojo(name="report",
	requiresDependencyResolution=ResolutionScope.RUNTIME_PLUS_SYSTEM,
	defaultPhase=LifecyclePhase.VALIDATE,
	requiresProject=true,
	threadSafe=true,
	requiresOnline=true)
public class ReportingMojo extends AbstractMojo {
	@Parameter(
		name="reportingServer", property="dvm.reportingServer", readonly=true, required=false
	)
	private String reportingServer;

	@Parameter(
		name="reportingKey", property="dvm.reportingKey", readonly=true, required=false
	)
	private String reportingKey;
	
	@Parameter( defaultValue = "${project}", readonly = true )
    private MavenProject project;
	
	private static final ObjectMapper mapper = new ObjectMapper();

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		final Project dvmProject = new Project(project.getGroupId(), project.getArtifactId(), project.getVersion());
		
		for (final Dependency mvnDependency : project.getDependencies()) {
			final Artifact artifact = new Artifact(mvnDependency.getGroupId(), mvnDependency.getArtifactId(), mvnDependency.getVersion());
			
			dvmProject.getDependencies().add(artifact);
		}
		
		try {
			final String serialized = serialize(dvmProject);
			
			getLog().info(serialized);
		} catch (final Exception e) {
			throw new MojoExecutionException("Error Serializing project: " + e.getMessage(), e);
		}
	}
	
	private static final String serialize(final Project dvmProject) throws JsonProcessingException {
		return mapper.writeValueAsString(dvmProject);
	}

}
