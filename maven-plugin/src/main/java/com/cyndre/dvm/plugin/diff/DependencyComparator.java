package com.cyndre.dvm.plugin.diff;

import java.util.Comparator;

import org.apache.maven.model.Dependency;

import com.cyndre.dvm.data.Comparators;
import com.google.common.collect.ComparisonChain;

public class DependencyComparator implements Comparator<Dependency> {
	/**
	 * Compare two dependencies by their group id, artifact id, and version
	 */
	@Override
	public int compare(final Dependency left, final Dependency right) {
		if (left == right) {
			return 0;
		}
		
		if (left == null) {
			return -1;
		} else if (right == null) {
			return 1;
		}
		
		return ComparisonChain.start()
			.compare(left.getGroupId(), right.getGroupId())
			.compare(left.getArtifactId(), right.getArtifactId())
			.compare(left.getVersion(), right.getVersion(), Comparators.VERSION_COMPARATOR)
		.result();
	}

}
