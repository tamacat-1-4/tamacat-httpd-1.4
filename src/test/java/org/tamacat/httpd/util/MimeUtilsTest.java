package org.tamacat.httpd.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MimeUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetContentType() {
		assertEquals("text/plain", MimeUtils.getContentType("test.txt"));
		assertEquals("text/html", MimeUtils.getContentType("test.html"));
		assertEquals("application/javascript", MimeUtils.getContentType("test.js"));
		assertEquals("text/css", MimeUtils.getContentType("test.css"));
		
		assertEquals("application/xml", MimeUtils.getContentType("test.xml"));
		assertEquals("application/json", MimeUtils.getContentType("test.json"));
		
		assertEquals(null, MimeUtils.getContentType(null));
		assertEquals(null, MimeUtils.getContentType(""));
		assertEquals("text/plain", MimeUtils.getContentType("text"));
		
		//add src/test/resources/mime-types.properties
		assertEquals("application/x-test", MimeUtils.getContentType("test.test"));
	}
}
