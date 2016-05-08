/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;


import static org.junit.Assert.assertEquals;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.util.HtmlUtils;

public class HtmlUtilsTest {

	private static Pattern pattern = HtmlUtils.CHARSET_PATTERN;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetCharset() {
		Header header = new BasicHeader("Content-Type", "text/html; charset=UTF-8");
		assertEquals("utf-8", HtmlUtils.getCharSet(header));
	}

	@Test
	public void testGetCharsetDefault() {
		Header header = new BasicHeader("Content-Type", "text/html");
		assertEquals(null, HtmlUtils.getCharSet(header));
	}

	@Test
	public void testGetCharSetFromMetaTag0() {
		String html1 = "<html><META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=utf-8\"></html>";
		Matcher matcher = pattern.matcher(html1);
		if (matcher.find()) {
			assertEquals("utf-8", matcher.group(4));
		}
	}

	@Test
	public void testGetCharSetFromMetaTag() {
		String html = "<html><META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\"></html>";
		String result = HtmlUtils.getCharSetFromMetaTag(html, "");
		assertEquals("utf-8", result);
	}

	@Test
	public void testGetCharSetFromMetaTag2() {
		String html = "<html><META HTTP-EQUIV='Content-Type' CONTENT='text/html; charset=UTF-8'></html>";
		String result = HtmlUtils.getCharSetFromMetaTag(html, "");
		assertEquals("utf-8", result);
	}

	@Test
	public void testGetCharSetFromMetaTag3() {
		String html = "<html><META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html;charset=UTF-8\"></html>";
		String result = HtmlUtils.getCharSetFromMetaTag(html, "");
		assertEquals("utf-8", result);
	}

	@Test
	public void testGetCharSetFromMetaTagDefault() {
		String html = "<html><META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html\"></html>";
		String result = HtmlUtils.getCharSetFromMetaTag(html, "utf-8");
		assertEquals("utf-8", result);
	}
}
