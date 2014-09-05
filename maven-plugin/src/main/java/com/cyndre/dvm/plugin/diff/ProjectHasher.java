package com.cyndre.dvm.plugin.diff;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;

public class ProjectHasher {
	public static String hashDirectoryContents(final File path)
	throws IOException {
		final MessageDigest digest;
		
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		
		final Iterator<File> files = FileUtils.iterateFiles(path, null, true);
		
		while(files.hasNext()) {
			final File f = files.next();

			MappedByteBuffer buffer = null;
			try (
					final RandomAccessFile rFile = new RandomAccessFile(f, "r");
					final FileChannel channel = rFile.getChannel();
			) {
				buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
				buffer.load();
				
				digest.update(buffer);
			} finally {
				if (buffer != null) {
					buffer.clear();
				}
			}
		}
		
		return Hex.encodeHexString(digest.digest());
	}
}
