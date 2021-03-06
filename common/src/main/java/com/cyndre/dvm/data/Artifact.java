package com.cyndre.dvm.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Artifact implements Comparable<Artifact> {
	
	
	private String groupId;
	private String artifactId;
	private String version;

	public Artifact() {
		
	}
	
	public Artifact(final String groupId, final String artifactId) {
		this.groupId = groupId;
		this.artifactId = artifactId;
	}
	
	public Artifact(final String groupId, final String artifactId, final String version) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}
	
	public Artifact(final Artifact artifact) {
		this(artifact.groupId, artifact.artifactId, artifact.version);
	}
	
	public String getVersion() {
		return version;
	}
	public void setVersion(final String version) {
		this.version = version;
	}
	
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(final String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(final String artifactId) {
		this.artifactId = artifactId;
	}
	
	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof Artifact)) {
			return false;
		}
		
		final Artifact rhs = (Artifact)o;
		
		return new EqualsBuilder()
			.append(this.getGroupId(), rhs.getGroupId())
			.append(this.getArtifactId(), rhs.getArtifactId())
			.append(this.getVersion(), rhs.getVersion())
		.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getGroupId())
			.append(this.getArtifactId())
			.append(this.getVersion())
		.toHashCode();
	}
	
	@Override
	public String toString() {
		return this.getGroupId() + ":" + this.getArtifactId() + ":" + this.getVersion();
	}

	@Override
	public int compareTo(Artifact o) {
		return Comparators.ARTIFACT_COMPARATOR.compare(this, o);
	}
}
