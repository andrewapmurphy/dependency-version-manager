package com.cyndre.dvm.plugin.diff;

import java.io.File;
import java.util.Collection;

import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingResult;

public class ProjectBuilderHelper {
	public static MavenProject buildProject(final ProjectBuilder pb, final File pomFile)
	throws MojoExecutionException {
		try {
			final ProjectBuildingResult result = pb.build(pomFile, new DefaultProjectBuildingRequest());
			
			final Collection<ModelProblem> problems = result.getProblems();
			
			if (problems != null && !problems.isEmpty()) {
				throw new MojoExecutionException(buildProblemString(problems));
			}
			
			return result.getProject();
		} catch (Exception e) {
			throw new MojoExecutionException("Unable to build project: " + e.getMessage(), e);
		}
	}
	
	private static String buildProblemString(final Collection<ModelProblem> problems) {
		final StringBuilder sb = new StringBuilder();
		
		for (final ModelProblem problem : problems) {
			sb.append(problem.toString()).append('\n');
		}
		
		return sb.toString();
	}
}