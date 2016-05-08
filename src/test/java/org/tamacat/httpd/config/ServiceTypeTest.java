/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config;

import static org.junit.Assert.*;

import org.junit.Test;

public class ServiceTypeTest {

	@Test
	public void testFind() {
		assertEquals(ServiceType.NORMAL, ServiceType.find("normal"));
		assertEquals(ServiceType.REVERSE, ServiceType.find("reverse"));
		assertEquals(ServiceType.LB, ServiceType.find("lb"));
		assertEquals(ServiceType.ERROR, ServiceType.find("error"));
		
		try {
			ServiceType.find("test");
			fail();
		} catch (IllegalArgumentException e) {
			assertTrue(true);
		}
	}
}
