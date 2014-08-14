package com.cyndre.dvm.plugin.diff;

import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

import com.google.common.base.Predicate;

public class DiffFilters {
	public static final String SCOPE_TEST = "test";
	
	public static Predicate<Dependency> IS_TEST_SCOPE = new Predicate<Dependency>() {
		@Override public boolean apply(Dependency dep) {
			return dep != null && SCOPE_TEST.equalsIgnoreCase(dep.getScope());
		}
	};
	
	public static Predicate<Dependency> IS_NOT_TEST_SCOPE = new Predicate<Dependency>() {
		@Override public boolean apply(Dependency dep) {
			return !IS_TEST_SCOPE.apply(dep);
		}
	};
	
	public static Predicate<Dependency> createIsModulePredicate(final MavenProject project) {
		if (project == null) {
			throw new IllegalArgumentException("project cannot be null!");
		}
		
		final String parentGroupId = project.getGroupId() + "." + project.getArtifactId();
		
		return new Predicate<Dependency>() {
			@Override
			public boolean apply(Dependency dep) {
				return dep != null
					&& parentGroupId.equalsIgnoreCase(dep.getGroupId())
					&& project.getModules().contains(dep.getArtifactId());
			}
		};
	}
}
