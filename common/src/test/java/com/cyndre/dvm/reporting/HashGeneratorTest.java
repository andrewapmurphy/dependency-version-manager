package com.cyndre.dvm.reporting;

import static org.junit.Assert.*;

import org.junit.Test;

public class HashGeneratorTest {
	private static final String KEY_1 = "key1";
	private static final String KEY_2 = "key2";
	
	private static final String DATA_1 = "data1";
	private static final String DATA_2 = "data2";

	@Test
	public void testRepeatable() {
		final HashGenerator hashKey1 = new HashGenerator(KEY_1);
		final HashGenerator hashKey2 = new HashGenerator(KEY_2);
		
		final String hash1 = hashKey1.hash(DATA_1);
		final String hash2 = hashKey2.hash(DATA_2);
		
		assertEquals(hash1, hashKey1.hash(DATA_1));
		assertNotSame(hash1, hashKey1.hash(DATA_2));
		
		assertEquals(hash2, hashKey2.hash(DATA_2));
		assertNotSame(hash2, hashKey2.hash(DATA_2));
		
		assertNotSame(hashKey1.hash(DATA_1), hashKey2.hash(DATA_1));
		assertNotSame(hashKey1.hash(DATA_2), hashKey2.hash(DATA_2));
	}

}
