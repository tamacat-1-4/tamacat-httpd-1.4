/*
 * Copyright (c) 2013, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpRequest;

public class DigestUtils {

	/**
	 * https://svn.apache.org/repos/asf/httpcomponents/httpclient/trunk/module-
	 * client/src/main/java/org/apache/http/impl/auth/DigestScheme.java Hexa
	 * values used when creating 32 character long digest in HTTP DigestScheme
	 * in case of authentication.
	 * 
	 * @see #encode(byte[])
	 */
	static final char[] HEXADECIMAL = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String getHashedPassword(HttpRequest request, AuthUser user, Digest digest) {
		if (user.isEncrypted() == false) {
			// A1 = username:realm:password
			String a1 = user.getAuthUsername() + ":" + digest.getRealm() + ":"
					+ user.getAuthPassword();
			String hash1 = DigestUtils.encode(DigestUtils.getMD5(a1));

			// A2 = Method:URI
			String a2 = request.getRequestLine().getMethod() + ":"
					+ request.getRequestLine().getUri();
			String hash2 = DigestUtils.encode(DigestUtils.getMD5(a2));

			// Digest = A1:nonce:nonce-count:cnonce:qop:A2
			String digestPassword = hash1 + ":" + digest.getNonce() + ":"
					+ digest.getNc() + ":" + digest.getCnonce() + ":"
					+ digest.getQop() + ":" + hash2;
			return DigestUtils.encode(DigestUtils.getMD5(digestPassword));
		} else {
			throw new UnsupportedOperationException(
					"Can not use encrypted password from Digest Authorization.");
		}
	}

	/**
	 * https://svn.apache.org/repos/asf/httpcomponents/httpclient/trunk/module-
	 * client/src/main/java/org/apache/http/impl/auth/DigestScheme.java
	 * 
	 * Encodes the 128 bit (16 bytes) MD5 digest into a 32 characters long
	 * <CODE>String</CODE> according to RFC 2617.
	 * 
	 * @param binaryData
	 *            array containing the digest
	 * @return encoded MD5, or <CODE>null</CODE> if encoding failed
	 */
	public static String encode(byte[] binaryData) {
		int n = binaryData.length;
		char[] buffer = new char[n * 2];
		for (int i = 0; i < n; i++) {
			int low = (binaryData[i] & 0x0f);
			int high = ((binaryData[i] & 0xf0) >> 4);
			buffer[i * 2] = HEXADECIMAL[high];
			buffer[(i * 2) + 1] = HEXADECIMAL[low];
		}
		return new String(buffer);
	}

	public static byte[] getMD5(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return md.digest(plainText.getBytes());
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
}
