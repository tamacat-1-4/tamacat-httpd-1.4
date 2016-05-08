/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core.ssl;

import junit.framework.TestCase;

import org.junit.Test;
import org.tamacat.httpd.core.ssl.KeyStoreType;

public class KeyStoreTypeTest extends TestCase {

	@Test
	public void testValueOf() {
		assertEquals(KeyStoreType.JKS, KeyStoreType.valueOf("JKS"));
		assertEquals(KeyStoreType.PKCS12, KeyStoreType.valueOf("PKCS12"));
		try {
			KeyStoreType.valueOf("TEST");
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

}
