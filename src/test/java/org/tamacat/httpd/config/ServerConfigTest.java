/*
 * Copyright (c) 2009 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServerConfigTest {

	ServerConfig config;

	@Before
	public void setUp() throws Exception {
		config = new ServerConfig();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetSocketTimeout() {
		assertEquals(config.getSocketTimeout(), 30000);

		config.setParam("ServerSocketTimeout", "100000");
		assertEquals(config.getSocketTimeout(), 100000);
	}

	@Test
	public void testGetConnectionTimeout() {
		assertEquals(config.getConnectionTimeout(), 30000);

		config.setParam("ConnectionTimeout", "180000");
		assertEquals(config.getConnectionTimeout(), 180000);
	}

	@Test
	public void testGetParamString() {
		assertEquals("url-config.xml", config.getParam("url-config.file"));
		assertEquals(null, config.getParam("none"));
	}

	@Test
	public void testGetParamStringT() {
		config.setParam("Port", "80");
		assertTrue(80 == config.getParam("Port", 9999));
		assertEquals("url-config.xml", config.getParam("url-config.file", ""));

		assertTrue(9999 == config.getParam("test", 9999));
		assertTrue(9999L == config.getParam("test", 9999L));
		assertTrue(9999d == config.getParam("test", 9999d));
		assertTrue(9999f == config.getParam("test", 9999f));
		assertTrue('c' == config.getParam("test", 'c'));
		assertEquals("9999", config.getParam("test", "9999"));

		assertEquals("80", config.getParam("Port", null));
		assertNull(config.getParam("test", null));
	}
	
	@Test
	public void testGetHttpsSupportProtocols() {
		// https.support-protocols=TLSv1,TLSv1.1,TLSv1.2
		config = new ServerConfig(new Properties());
		assertTrue(0 == config.getHttpsSupportProtocols().length);

		config.setParam("https.support-protocols", "");
		assertTrue(0 == config.getHttpsSupportProtocols().length);

		config.setParam("https.support-protocols", "TLSv1");
		assertTrue(1 == config.getHttpsSupportProtocols().length);
		assertEquals("TLSv1", config.getHttpsSupportProtocols()[0]);

		config.setParam("https.support-protocols", "TLSv1,TLSv1.1");
		assertTrue(2 == config.getHttpsSupportProtocols().length);
		assertEquals("TLSv1", config.getHttpsSupportProtocols()[0]);
		assertEquals("TLSv1.1", config.getHttpsSupportProtocols()[1]);

		config.setParam("https.support-protocols", "TLSv1,TLSv1.1,TLSv1.2");
		assertTrue(3 == config.getHttpsSupportProtocols().length);

		assertEquals("TLSv1", config.getHttpsSupportProtocols()[0]);
		assertEquals("TLSv1.1", config.getHttpsSupportProtocols()[1]);
		assertEquals("TLSv1.2", config.getHttpsSupportProtocols()[2]);
	}
}
