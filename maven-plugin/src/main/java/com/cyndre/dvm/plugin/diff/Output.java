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
	
	public static final String toReadableOnlyLeft(final Map<String, String> diff) {
		final Map<String, String> readableEntries = Maps.transformEntries(diff, new Maps.EntryTransformer<String, String, String>() {
			@Override public String transformEntry(String key, String version) {				
				final String strVersion = (version != null ? version : NOT_PRESENT);
				return String.format(OUTPUT_FORMAT, key, strVersion, EMPTY);
			}
		});
		
		return StringUtils.join(readableEntries.values(), "\n");
	}
	
	public static final String toReadableOnlyRight(final Map<String, String> diff) {
		if (diff == null || diff.isEmpty()) {
			return "";
		}
		
		final Map<String, String> readableEntries = Maps.transformEntries(diff, new Maps.EntryTransformer<String, String, String>() {
			@Override public String transformEntry(final String key, final String version) {				
				final String strVersion = (version != null ? version : NOT_PRESENT);
				return String.format(OUTPUT_FORMAT, key, EMPTY, strVersion);
			}
		});
		
		return StringUtils.join(readableEntries.values(), "\n") + "\n";
	}
	
	public static final String toReadableString(final Map<String, ValueDifference<String>> diff) {
		if (diff == null || diff.isEmpty()) {
			return "";
		}
		
		final Map<String, String> readableEntries = Maps.transformEntries(diff, new Maps.EntryTransformer<String, ValueDifference<String>, String>() {
			@Override public String transformEntry(String key, ValueDifference<String> value) {				
				final String oldVersion = value.leftValue()  != null ? value.leftValue()  : NOT_PRESENT;
				final String newVersion = value.rightValue() != null ? value.rightValue() : NOT_PRESENT;

				return String.format(OUTPUT_FORMAT, key, oldVersion, newVersion);
			}
		});
		
		return StringUtils.join(readableEntries.values(), "\n") + "\n";
	}
	
	public static final String toReadableString(final Collection<String> deps) {
		return "";
		/*if (deps == null || deps.isEmpty()) {
			return "";
		}
		
		return StringUtils.join(Collections2.transform(deps,
			new Function<Dependency, String>() {
				@Override public String apply(Dependency dep) {
					return versionlessKey(dep) + ":" + dep.getVersion();
				}
			}),
			"\n"
		) + "\n";*/
	}
	
	public static final String versionlessKey(final Dependency dependency) {
		return ArtifactUtils.versionlessKey(dependency.getGroupId(), dependency.getArtifactId());
	}
	
	public static final String versionlessKey(final Artifact dependency) {
		return ArtifactUtils.versionlessKey(dependency.getGroupId(), dependency.getArtifactId());
	}
}
