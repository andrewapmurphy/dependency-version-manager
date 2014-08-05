package com.cyndre.dvm.data;

import java.util.Comparator;

import org.apache.maven.artifact.versioning.ComparableVersion;

public class Comparators {
	public static final Comparator<Artifact> ARTIFACT_COMPARATOR = new Comparator<Artifact>() {
		@Override public int compare(Artifact left, Artifact right) {
			if (left == right) {
				return 0;
			} else if (left == null && right != null) {
				return -1;
			} else if (left != null && right == null) {
				return 1;
			}
			
			final int groupOrder = NAME_COMPARATOR.compare(left.getGroupId(), right.getGroupId());
			
			if (groupOrder != 0) {
				return groupOrder;
			}
			
			final int artifactOrder = NAME_COMPARATOR.compare(left.getArtifactId(), right.getArtifactId());
			
			if (artifactOrder != 0) {
				return artifactOrder;
			}
			
			return VERSION_COMPARATOR.compare(left.getVersion(), right.getVersion());
		}
	};
	
	public static final Comparator<String> NAME_COMPARATOR = new Comparator<String>() {
		@Override public int compare(String left, String right) {
			if (left == right) {
				return 0;
			} else if (left == null && right != null) {
				return -1;
			} else if (left != null && right == null) {
				return 1;
			}
			
			return left.compareToIgnoreCase(right);
		}
	};
	
	public static final Comparator<String> VERSION_COMPARATOR = new Comparator<String>() {
		@Override public int compare(String left, String right) {
			if (left == right) {
				return 0;
			} else if (left == null && right != null) {
				return -1;
			} else if (left != null && right == null) {
				return 1;
			}
			
			ComparableVersion leftVersion = new ComparableVersion(left);
			ComparableVersion rightVersion = new ComparableVersion(right);
			
			return leftVersion.compareTo(rightVersion);
		}
	};
}
