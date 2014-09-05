package com.cyndre.dvm.plugin.diff;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.model.Dependency;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.collect.MapDifference.ValueDifference;

public class Output {
	private static final String NOT_PRESENT = "Not present";
	public static final String OUTPUT_FORMAT = "%s\t%s\t\t%s";
	private static final String EMPTY = "";
	
	public static final String toReadableOnlyLeft(final Map<String, Dependency> diff) {
		final Map<String, String> readableEntries = Maps.transformEntries(diff, new Maps.EntryTransformer<String, Dependency, String>() {
			@Override public String transformEntry(String key, Dependency dependency) {				
				final String version = dependency != null ? dependency.getVersion() : NOT_PRESENT;
				return String.format(OUTPUT_FORMAT, key, version, EMPTY);
			}
		});
		
		return StringUtils.join(readableEntries.values(), "\n");
	}
	
	public static final String toReadableOnlyRight(final Map<String, Dependency> diff) {
		if (diff == null || diff.isEmpty()) {
			return "";
		}
		
		final Map<String, String> readableEntries = Maps.transformEntries(diff, new Maps.EntryTransformer<String, Dependency, String>() {
			@Override public String transformEntry(String key, Dependency dependency) {				
				final String version = dependency != null ? dependency.getVersion() : NOT_PRESENT;
				return String.format(OUTPUT_FORMAT, key, EMPTY, version);
			}
		});
		
		return StringUtils.join(readableEntries.values(), "\n") + "\n";
	}
	
	public static final String toReadableString(final Map<String, ValueDifference<Dependency>> diff) {
		if (diff == null || diff.isEmpty()) {
			return "";
		}
		
		final Map<String, String> readableEntries = Maps.transformEntries(diff, new Maps.EntryTransformer<String, ValueDifference<Dependency>, String>() {
			@Override public String transformEntry(String key, ValueDifference<Dependency> value) {				
				final Dependency oldDependency = value.leftValue();
				final String oldVersion = oldDependency != null ? oldDependency.getVersion() : NOT_PRESENT;
				
				final Dependency newDependency = value.rightValue();
				final String newVersion = newDependency != null ? newDependency.getVersion() : NOT_PRESENT;
				
				
				return String.format(OUTPUT_FORMAT, key, oldVersion, newVersion);
			}
		});
		
		return StringUtils.join(readableEntries.values(), "\n") + "\n";
	}
	
	public static final String toReadableString(final Collection<Dependency> deps) {
		if (deps == null || deps.isEmpty()) {
			return "";
		}
		
		return StringUtils.join(Collections2.transform(deps,
			new Function<Dependency, String>() {
				@Override public String apply(Dependency dep) {
					return versionlessKey(dep) + ":" + dep.getVersion();
				}
			}),
			"\n"
		) + "\n";
	}
	
	public static final String versionlessKey(final Dependency dependency) {
		return ArtifactUtils.versionlessKey(dependency.getGroupId(), dependency.getArtifactId());
	}
	
	public static final String versionlessKey(final Artifact dependency) {
		return ArtifactUtils.versionlessKey(dependency.getGroupId(), dependency.getArtifactId());
	}
}
