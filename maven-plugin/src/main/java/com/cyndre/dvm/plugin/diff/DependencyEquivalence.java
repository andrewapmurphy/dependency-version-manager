package com.cyndre.dvm.plugin.diff;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.maven.model.Dependency;

import com.google.common.base.Equivalence;

public class DependencyEquivalence extends Equivalence<Dependency> {

	@Override
	protected boolean doEquivalent(Dependency left, Dependency right) {
		return new EqualsBuilder()
			.append(left.getGroupId(), right.getGroupId())
			.append(left.getArtifactId(), right.getArtifactId())
			.append(left.getVersion(), right.getVersion())
		.isEquals();
	}

	@Override
	protected int doHash(Dependency dep) {
		return new HashCodeBuilder()
			.append(dep.getGroupId())
			.append(dep.getArtifactId())
			.append(dep.getVersion())
		.toHashCode();
	}

}
