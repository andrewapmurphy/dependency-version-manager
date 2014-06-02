package com.cyndre.dvm.data;

import java.util.HashSet;
import java.util.Set;

public class Project extends Artifact {
	private final Set<Artifact> dependencies;
	
	public Project() {
		this(null, null, null);
	}

	public Project(final String groupId, final String artifactId) {
		this(groupId, artifactId, null);
	}
	
	public Project(final String groupId, final String artifactId, final String version) {
		this(groupId, artifactId, version, new HashSet<Artifact>());
	}
	
	public Project(final String groupId, final String artifactId, final String version, final Set<Artifact> dependencies) {
		super(groupId, artifactId, version);
		this.dependencies = new HashSet<>(dependencies);
	}

	public Set<Artifact> getDependencies() {
		return dependencies;
	}
}
