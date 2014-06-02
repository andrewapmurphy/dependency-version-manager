package com.cyndre.dvm.data;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Project {
	private long projectId;

	private String groupId;
	private String artifactId;
	private String version;
	
	private Date firstSeen;
	private Date lastUpdated;
	
	public Date getFirstSeen() {
		return firstSeen;
	}
	public void setFirstSeen(Date firstSeen) {
		this.firstSeen = firstSeen;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public long getProjectId() {
		return projectId;
	}
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
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
	
	public boolean isSameProject(final Project rhs) {
		if (rhs == null) {
			return false;
		}
		
		return new EqualsBuilder()
			.append(groupId, rhs.getGroupId())
			.append(artifactId, rhs.getArtifactId())
		.isEquals();
	}
	
	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof Project)) {
			return false;
		}
		
		final Project rhs = (Project)o;
		
		return new EqualsBuilder()
			.append(groupId, rhs.getGroupId())
			.append(artifactId, rhs.getArtifactId())
			.append(version, rhs.version)
		.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(groupId)
			.append(artifactId)
			.append(version)
		.toHashCode();
	}
	
	@Override
	public String toString() {
		return groupId + ":" + artifactId + ":" + version;
	}
}
