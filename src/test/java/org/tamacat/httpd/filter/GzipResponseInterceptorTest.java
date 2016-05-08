/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import static org.junit.Assert.*;

import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.junit.Test;

public class GzipResponseInterceptorTest {

	@Test
	public void testSetContentType() {
		GzipResponseInterceptor target = new GzipResponseInterceptor();
		target.setContentType("text/html");
		target.setContentType(" text/x-html ");
		target.setContentType("html,plain,css,javascript");
		target.setContentType("");
		target.setContentType(null);
	}
	
	@Test
	public void testUseCompress() {
		GzipResponseInterceptor target = new GzipResponseInterceptor();

		//all types -> true
		assertTrue(target.useCompress(new BasicHeader(HTTP.CONTENT_TYPE, "text/html")));
		assertTrue(target.useCompress(new BasicHeader(HTTP.CONTENT_TYPE, "text/plain")));
		assertTrue(target.useCompress(new BasicHeader(HTTP.CONTENT_TYPE, "image/jpeg")));
		
		//html only
		target.setContentType("text/html");
		assertTrue(target.useCompress(new BasicHeader(HTTP.CONTENT_TYPE, "text/html")));
		assertTrue(target.useCompress(new BasicHeader(HTTP.CONTENT_TYPE, "text/html; charset=UTF8")));
		assertFalse(target.useCompress(new BasicHeader(HTTP.CONTENT_TYPE, "text/plain")));
		assertFalse(target.useCompress(new BasicHeader(HTTP.CONTENT_TYPE, "image/jpeg")));
		
		//invalid header -> false
		assertFalse(target.useCompress(new BasicHeader(HTTP.CONTENT_TYPE, "jpeg")));
		assertFalse(target.useCompress(new BasicHeader(HTTP.CONTENT_TYPE, "")));

		//header is null -> false
		assertFalse(target.useCompress(null));
		assertFalse(target.useCompress(new BasicHeader(HTTP.CONTENT_TYPE, null)));
		
		target.setContentType(" html, css, javascript ");
		assertTrue(target.useCompress(new BasicHeader(HTTP.CONTENT_TYPE, "text/html")));
		assertTrue(target.useCompress(new BasicHeader(HTTP.CONTENT_TYPE, "text/html; charset=UTF8")));
		assertTrue(target.useCompress(new BasicHeader(HTTP.CONTENT_TYPE, "text/javascript")));
		assertTrue(target.useCompress(new BasicHeader(HTTP.CONTENT_TYPE, "text/css")));
		assertFalse(target.useCompress(new BasicHeader(HTTP.CONTENT_TYPE, "text/plain")));
		assertFalse(target.useCompress(new BasicHeader(HTTP.CONTENT_TYPE, "image/jpeg")));
	}
}
