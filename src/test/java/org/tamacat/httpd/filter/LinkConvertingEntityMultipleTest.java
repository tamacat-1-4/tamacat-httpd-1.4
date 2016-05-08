/*
 * Copyright (c) 2010, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.util.regex.Pattern;

import org.apache.http.entity.StringEntity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.filter.LinkConvertingEntity;

public class LinkConvertingEntityMultipleTest {
	Pattern[] patterns;
	
	@Before
	public void setUp() throws Exception {
		patterns = new Pattern[] {
			Pattern.compile("<[^<]*\\s+(href|src|action|.*[0-9]*;?url)=(?:\'|\")?([^('|\")]*)(?:\'|\")?[^>]*>", Pattern.CASE_INSENSITIVE),
			Pattern.compile("<script[^<]*>*(location.href|location.pathname)=(?:\'|\")?([^('|\")]*)(?:\'|\")?[^>]*</script>", Pattern.CASE_INSENSITIVE)
		};
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testWriteToOutputStream() throws Exception {

		StringEntity html = new StringEntity("<html><script type=\"text/javascript\"> location.href=\"/aaa/index.html\";</script><a href=\"/aaa/test.html\">aaa</a></html>\r\n");
		String before = "/aaa/";
		String after = "/bbb/";
		LinkConvertingEntity entity = new LinkConvertingEntity(html, before, after, patterns);
		assertNotNull(entity);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		entity.writeTo(out);
		//System.out.println(new String(out.toByteArray()));

		assertEquals(html.getContentLength(), entity.getContentLength());
		assertEquals("<html><script type=\"text/javascript\"> location.href=\"/bbb/index.html\";</script><a href=\"/bbb/test.html\">aaa</a></html>\r\n", new String(out.toByteArray()));
	}
	
	@Test
	public void testWriteToOutputStream2() throws Exception {
		StringEntity html = new StringEntity("<html><a href=\"/aaaaa/test.html\">aaa</a></html>\r\n");
		String before = "/aaaaa/";
		String after = "/bbb/";
		LinkConvertingEntity entity = new LinkConvertingEntity(html, before, after, patterns);
		assertNotNull(entity);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		entity.writeTo(out);
		assertEquals(html.getContentLength()-2, entity.getContentLength());
		assertEquals("<html><a href=\"/bbb/test.html\">aaa</a></html>\r\n", new String(out.toByteArray()));
	}
	
	@Test
	public void testWriteToOutputStream3() throws Exception {
		StringEntity html = new StringEntity("<html><a href=\"/aaa/test.html\">aaa</a></html>\r\n");
		String before = "/aaa/";
		String after = "/bbbbb/";
		LinkConvertingEntity entity = new LinkConvertingEntity(html, before, after, patterns);
		assertNotNull(entity);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		entity.writeTo(out);
		assertEquals(html.getContentLength()+2, entity.getContentLength());
		assertEquals("<html><a href=\"/bbbbb/test.html\">aaa</a></html>\r\n", new String(out.toByteArray()));
	}
	
	
	@Test
	public void testWriteToOutputStream_NAME() throws Exception {
		StringEntity html = new StringEntity("<html><a href=\"/aaa/test.html\">/aaa/</a></html>\r\n");
		String before = "/aaa/";
		String after = "/bbbbb/";
		LinkConvertingEntity entity = new LinkConvertingEntity(html, before, after, patterns);
		assertNotNull(entity);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		entity.writeTo(out);
		assertEquals(html.getContentLength()+2, entity.getContentLength());
		assertEquals("<html><a href=\"/bbbbb/test.html\">/aaa/</a></html>\r\n", new String(out.toByteArray()));
	}
	
	@Test
	public void testWriteToOutputStream_ERROR() throws Exception {
		StringEntity html = new StringEntity("<html><a href=\"/test.html\">/aaaaa/</a></html>\r\n");
		String before = "/aaaaa/";
		String after = "/bbbbb/";
		LinkConvertingEntity entity = new LinkConvertingEntity(html, before, after, patterns);
		assertNotNull(entity);
		
		try {
			entity.writeTo(null);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("Output stream may not be null", e.getMessage());
		}
	}
}
