package com.cyndre.dvm.reporting;

import java.io.UnsupportedEncodingException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class HashGenerator {
	private String ALGORITHM = "HmacSHA256";
	private String CHARACTER_ENCODING = "UTF-8";
	
	private final Mac mac;
	
	/**
	 * 
	 * @param secret
	 *  Secret key used to generate the hash
	 */
	public HashGenerator(final String secret) {
		final SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), ALGORITHM);
		
		try {
			this.mac = Mac.getInstance(ALGORITHM);
			this.mac.init(keySpec);
		} catch (Exception e) {
			throw new IllegalStateException("Could not initialize hash generator: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Hash a given string
	 * 
	 * Hashes a string and resets the state of the MAC to ensure
	 * that another digest can be safely generated
	 * 
	 * @param data
	 *  data to hash
	 * @return
	 *  hexidecimal string representing the hashed data
	 * @throws Exception
	 */
	public String hash(final String data) { 
		try {
			final byte[] raw = data.getBytes(CHARACTER_ENCODING);
			final byte[] digest = this.mac.doFinal(raw);
			
			this.mac.reset();
			
			return DatatypeConverter.printHexBinary(digest);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Error hashing: " + e.getMessage(), e);
		}
	}
}
