package com.cyndre.dvm.plugin;

import java.util.concurrent.TimeUnit;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
import com.cyndre.dvm.reporting.HashGenerator;
import com.cyndre.dvm.reporting.ReportingConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Generate a dependency report and submit it to a manament server
 */
@Mojo(name="report",
	requiresDependencyResolution=ResolutionScope.RUNTIME_PLUS_SYSTEM,
	defaultPhase=LifecyclePhase.VALIDATE,
	requiresProject=true,
	threadSafe=true,
	requiresOnline=true)
public class ReportMojo extends AbstractMojo {
	private static final String REQUEST_CONTENT_TYPE = "application/json";
	
	/**
	 * Url the project report will be submitted to
	 */
	@Parameter(
		name="reportingUrl", property="dvm.reportingUrl", readonly=true, required=true
	)
	private String reportingUrl;

	/**
	 * Secret key used to generate the reporting hash
	 * 
	 * The generated hash submitted by this mojo must match the hash generated
	 * by the server using the shared private key specified by this parameter.
	 * 
	 */
	@Parameter(
		name="reportingKey", property="dvm.reportingKey", readonly=true, required=true
	)
	private String reportingKey;
	
	@Parameter(defaultValue="${project}", readonly=true)
    private MavenProject project;
	
	private static final ObjectMapper mapper = new ObjectMapper();

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		final Project dvmProject = new Project(
			project.getGroupId(),
			project.getArtifactId(),
			project.getVersion(),
			project.getName(),
			project.getDescription()
		);
		
		for (final Dependency mvnDependency : project.getDependencies()) {
			final Artifact artifact = new Artifact(mvnDependency.getGroupId(), mvnDependency.getArtifactId(), mvnDependency.getVersion());
			
			dvmProject.getDependencies().add(artifact);
		}
		
		report(dvmProject);
	}
	
	private void report(final Project dvmProject) throws MojoExecutionException {
		try {
			final String serializedProject = serialize(dvmProject);
			
			getLog().debug("Submitting: " + serializedProject);
			
			final HttpUriRequest request = createRequest(reportingUrl, reportingKey, serializedProject);
			
			submit(request);
		} catch (final Exception e) {
			throw new MojoExecutionException("Error Serializing project: " + e.getMessage(), e);
		}
	}
	
	private void submit(final HttpUriRequest request) throws Exception {
		getLog().debug("Submitting report to: " + reportingUrl);
		final long startTime = System.currentTimeMillis();
			
		try (final CloseableHttpClient client = HttpClients.createDefault()) {
			
			try (CloseableHttpResponse response = client.execute(request)) {
				final int statusCode = response.getStatusLine().getStatusCode();
				
				getLog().debug("Status code for report: " + statusCode);
				
				if (!wasSuccess(statusCode)) {
					throw new MojoExecutionException("Error reporting: server returned status code " + statusCode);
				}
			}
		} finally {
			final long duration = System.currentTimeMillis() - startTime;

			getLog().debug("Done reporting");
			
			final long seconds   = TimeUnit.MILLISECONDS.toSeconds(duration);
			final long remaining = duration - (seconds * TimeUnit.SECONDS.toMillis(1));
			
			getLog().debug("Time to submit report: " + seconds + "." + remaining);
		}
	}
	
	private HttpUriRequest createRequest(final String reportingUrl, final String reportingKey, final String serializedProject) {
		final HttpPost request = new HttpPost(reportingUrl);
		
		final String hash = generateHash(serializedProject, reportingKey);
		
		getLog().debug(String.format("data='%s' %s\nkey='%s' %s\nhash='%s'", serializedProject, serializedProject.hashCode(), reportingKey, reportingKey.hashCode(), hash));
		request.setHeader(ReportingConstants.DIGEST_HEADER, hash);
		
		final HttpEntity entity = new StringEntity(
			serializedProject,
			ContentType.create(REQUEST_CONTENT_TYPE, Consts.UTF_8)
		);

		request.setEntity(entity);
		
		return request;
	}
	
	private static boolean wasSuccess(final int statusCode) {
		return statusCode >= 200 && statusCode < 300;
	}
	
	private static String generateHash(final String data, final String secretKey) {
		final HashGenerator generator = new HashGenerator(secretKey);
		
		return generator.hash(data);
	}
	
	private static final String serialize(final Project dvmProject) throws JsonProcessingException {
		return mapper.writeValueAsString(dvmProject);
	}

}
