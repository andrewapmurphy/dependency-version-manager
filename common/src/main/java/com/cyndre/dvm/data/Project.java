package com.cyndre.dvm.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class Project extends Artifact {
	private final Set<Artifact> dependencies;
	private final String name;
	private final String description;
	
	public Project() {
		this(null, null, null, null, null);
	}

	public Project(final String groupId, final String artifactId) {
		this(groupId, artifactId, null, null, null);
	}
	
	public Project(final String groupId, final String artifactId, final String version, final String name, final String description) {
		this(groupId, artifactId, version, name, description, Collections.<Artifact>emptySet());
	}
	
	public Project(final String groupId, final String artifactId, final String version, final String name, final String description, final Set<Artifact> dependencies) {
		super(groupId, artifactId, version);
		this.dependencies = new TreeSet<>(Comparators.ARTIFACT_COMPARATOR);

		setDependencies(dependencies);
		
		this.name = name;
		this.description = description;
	}
	
	public Project(final Project project) {
		this(
			project.getGroupId(),
			project.getArtifactId(),
			project.getVersion(),
			project.getName(),
			project.getDescription(),
			project.getDependencies()
		);
	}

	public Set<Artifact> getDependencies() {
		return dependencies;
	}
	
	public void setDependencies(final Collection<Artifact> dependencies) {
		if (!this.dependencies.isEmpty()) {
			this.dependencies.clear();			
		}
		
		if (dependencies != null && !dependencies.isEmpty()) {
			this.dependencies.addAll(dependencies);
		}
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
}
