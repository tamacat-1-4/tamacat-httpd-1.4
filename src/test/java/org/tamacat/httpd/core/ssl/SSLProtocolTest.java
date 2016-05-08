/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core.ssl;


import junit.framework.TestCase;

import org.junit.Test;
import org.tamacat.httpd.core.ssl.SSLProtocol;

public class SSLProtocolTest extends TestCase {

	@Test
	public void testValueOf() {
		assertEquals(SSLProtocol.SSL, SSLProtocol.valueOf("SSL"));
		assertEquals(SSLProtocol.SSLv2, SSLProtocol.valueOf("SSLv2"));
		assertEquals(SSLProtocol.SSLv3, SSLProtocol.valueOf("SSLv3"));
		assertEquals(SSLProtocol.TLS, SSLProtocol.valueOf("TLS"));
		assertEquals(SSLProtocol.TLSv1, SSLProtocol.valueOf("TLSv1"));
		assertEquals(SSLProtocol.TLSv1_1, SSLProtocol.valueOf("TLSv1_1"));
		try {
			SSLProtocol.valueOf("TLSv2");
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}

}
