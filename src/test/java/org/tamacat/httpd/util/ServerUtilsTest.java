package org.tamacat.httpd.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServerUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetServerDocsRoot() {
		String serverHome = System.getProperty("server.home");
		String userDir = System.getProperty("user.dir");
		String home = serverHome != null ? serverHome : userDir;
		assertEquals((home + "/htdocs/root").replace("\\", "/"), ServerUtils.getServerDocsRoot("${server.home}/htdocs/root"));
	}
}
