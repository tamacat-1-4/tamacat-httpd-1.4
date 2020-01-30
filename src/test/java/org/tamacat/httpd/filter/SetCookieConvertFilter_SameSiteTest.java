/*
 * Copyright (c) 2018 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import static org.junit.Assert.*;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.mock.HttpObjectFactory;

/**
 * Test of Response Filter for Set-Cookie response header adding Secure/HttpOnly attributes.
 */
public class SetCookieConvertFilter_SameSiteTest {
	
	SetCookieConvertFilter filter;

	@Before
	public void setUp() throws Exception {
		ServerConfig config = new ServerConfig();
		ServiceUrl serviceUrl = new ServiceUrl(config);
		filter = new SetCookieConvertFilter();
		filter.init(serviceUrl);
		filter.setSecure(false);
		filter.setHttpOnly(false);
		filter.setSameSite("Strict");
		filter.setHttpSecureEnabled(false);
		filter.setUseForwardedProto(false);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * HttpOnly=true, Secure=false
	 */
	@Test
	public void testAfterResponseAddHttpOnly() {
		filter.setHttpOnly(true);
		filter.setSecure(false);
		filter.setCheckParentRequestHeader("X-Forwarded-For");
		filter.setHttpSecureEnabled(false);
		
		HttpRequest req = HttpObjectFactory.createHttpRequest("GET", "/");
		HttpResponse resp = HttpObjectFactory.createHttpResponse(200, "OK");
		resp.addHeader("Set-Cookie", "name1=value1; path=/test1");
		resp.addHeader("Set-Cookie", "name2=value2; path=/test2; HttpOnly;");
		resp.addHeader("Set-Cookie", "name3=value3; path=/test3; Secure");
		resp.addHeader("Set-Cookie", "name4=value4; path=/test4; HttpOnly; Secure");
		
		resp.addHeader("Set-Cookie", "nameHttpOnly=valueHttpOnly; path=/HttpOnly");
		
		resp.setHeader("Test","OK");
		HttpContext context = HttpObjectFactory.createHttpContext();
		filter.doFilter(req, resp, context);
		filter.afterResponse(req, resp, context);
		
		Header[] headers= resp.getHeaders("Set-Cookie");
		assertEquals("name1=value1; path=/test1; HttpOnly; SameSite=Strict", headers[0].getValue());
		assertEquals("name2=value2; path=/test2; HttpOnly; SameSite=Strict", headers[1].getValue());
		assertEquals("name3=value3; path=/test3; HttpOnly; SameSite=Strict", headers[2].getValue());
		assertEquals("name4=value4; path=/test4; HttpOnly; SameSite=Strict", headers[3].getValue());
		assertEquals("nameHttpOnly=valueHttpOnly; path=/HttpOnly; HttpOnly; SameSite=Strict", headers[4].getValue());	
	}

	/**
	 * HttpOnly=false, Secure=true
	 */
	@Test
	public void testAfterResponseAddSecure() {
		filter.setHttpOnly(false);
		filter.setSecure(true);
		filter.setCheckParentRequestHeader("X-Forwarded-For");
		filter.setHttpSecureEnabled(false);
		
		HttpRequest req = HttpObjectFactory.createHttpRequest("GET", "/");
		HttpResponse resp = HttpObjectFactory.createHttpResponse(200, "OK");
		resp.addHeader("Set-Cookie", "name1=value1; path=/test1");
		resp.addHeader("Set-Cookie", "name2=value2; path=/test2; HttpOnly;");
		resp.addHeader("Set-Cookie", "name3=value3; path=/test3; Secure");
		resp.addHeader("Set-Cookie", "name4=value4; path=/test4; HttpOnly; Secure");
		
		resp.addHeader("Set-Cookie", "nameSecure=valueSecure; path=/Secure");
		
		resp.setHeader("Test","OK");
		HttpContext context = HttpObjectFactory.createHttpContext();
		filter.doFilter(req, resp, context);
		filter.afterResponse(req, resp, context);
		
		Header[] headers= resp.getHeaders("Set-Cookie");
		assertEquals("name1=value1; path=/test1; Secure; SameSite=Strict", headers[0].getValue());
		assertEquals("name2=value2; path=/test2; HttpOnly; Secure; SameSite=Strict", headers[1].getValue());
		assertEquals("name3=value3; path=/test3; Secure; SameSite=Strict", headers[2].getValue());
		assertEquals("name4=value4; path=/test4; HttpOnly; Secure; SameSite=Strict", headers[3].getValue());
		assertEquals("nameSecure=valueSecure; path=/Secure; Secure; SameSite=Strict", headers[4].getValue());
	}
	
	/**
	 * HttpOnly=true, Secure=true
	 * httpSecureEnabled=true
	 * X-Forwarded-For: 127.0.0.1
	 */
	@Test
	public void testAfterResponseAddSecureOn() {
		filter.setHttpOnly(true);
		filter.setSecure(true);
		filter.setCheckParentRequestHeader("X-Forwarded-For");
		filter.setHttpSecureEnabled(true);
		
		HttpRequest req = HttpObjectFactory.createHttpRequest("GET", "/");
		req.setHeader("X-Forwarded-For", "127.0.0.1");
		
		HttpResponse resp = HttpObjectFactory.createHttpResponse(200, "OK");
		resp.addHeader("Set-Cookie", "name1=value1; path=/test1");
		resp.addHeader("Set-Cookie", "name2=value2; path=/test2; HttpOnly;");
		resp.addHeader("Set-Cookie", "name3=value3; path=/test3; Secure");
		resp.addHeader("Set-Cookie", "name4=value4; path=/test4; HttpOnly; Secure");
		
		resp.addHeader("Set-Cookie", "nameSecure=valueSecure; path=/Secure");
		
		resp.setHeader("Test","OK");
		HttpContext context = HttpObjectFactory.createHttpContext();
		filter.doFilter(req, resp, context);
		filter.afterResponse(req, resp, context);
		
		Header[] headers= resp.getHeaders("Set-Cookie");
		assertEquals("name1=value1; path=/test1; HttpOnly; Secure; SameSite=Strict", headers[0].getValue());
		assertEquals("name2=value2; path=/test2; HttpOnly; Secure; SameSite=Strict", headers[1].getValue());
		assertEquals("name3=value3; path=/test3; HttpOnly; Secure; SameSite=Strict", headers[2].getValue());
		assertEquals("name4=value4; path=/test4; HttpOnly; Secure; SameSite=Strict", headers[3].getValue());
		assertEquals("nameSecure=valueSecure; path=/Secure; HttpOnly; Secure; SameSite=Strict", headers[4].getValue());
	}
	
	/**
	 * HttpOnly=true, Secure=true
	 * httpSecureEnabled=true
	 * Empty X-Forwarded-For header.
	 */
	@Test
	public void testAfterResponseAddSecureOff_HTTP() {
		filter.setHttpOnly(true);
		filter.setSecure(true);
		filter.setCheckParentRequestHeader("X-Forwarded-For");
		filter.setHttpSecureEnabled(true);
		
		HttpRequest req = HttpObjectFactory.createHttpRequest("GET", "/");
		//req.setHeader("X-Forwarded-For", "127.0.0.1"); //Internal HTTP Access.
		
		HttpResponse resp = HttpObjectFactory.createHttpResponse(200, "OK");
		resp.addHeader("Set-Cookie", "name1=value1; path=/test1");
		resp.addHeader("Set-Cookie", "name2=value2; path=/test2; HttpOnly;");
		resp.addHeader("Set-Cookie", "name3=value3; path=/test3; Secure;");
		resp.addHeader("Set-Cookie", "name4=value4; path=/test4; HttpOnly; Secure");
		
		resp.addHeader("Set-Cookie", "nameSecure=valueSecure; path=/Secure");
		
		resp.setHeader("Test","OK");
		HttpContext context = HttpObjectFactory.createHttpContext();
		filter.doFilter(req, resp, context);
		filter.afterResponse(req, resp, context);
		
		Header[] headers= resp.getHeaders("Set-Cookie");
		assertEquals("name1=value1; path=/test1", headers[0].getValue());
		assertEquals("name2=value2; path=/test2; HttpOnly;", headers[1].getValue());
		assertEquals("name3=value3; path=/test3; Secure;", headers[2].getValue());
		assertEquals("name4=value4; path=/test4; HttpOnly; Secure", headers[3].getValue());
		assertEquals("nameSecure=valueSecure; path=/Secure", headers[4].getValue());
	}
	
	/**
	 * HttpOnly=true, Secure=true
	 * httpSecureEnabled=false (default)
	 * X-Forwarded-Proto: http
	 */
	@Test
	public void testAfterResponseForwarededProto_AddSecureOff_HTTP() {
		filter.setHttpOnly(true);
		filter.setSecure(true);
		filter.setUseForwardedProto(true);
		
		HttpRequest req = HttpObjectFactory.createHttpRequest("GET", "/");
		req.setHeader("X-Forwarded-Proto", "http"); //Internal HTTP Access.
		
		HttpResponse resp = HttpObjectFactory.createHttpResponse(200, "OK");
		resp.addHeader("Set-Cookie", "name1=value1; path=/test1");
		resp.addHeader("Set-Cookie", "name2=value2; path=/test2; HttpOnly;");
		resp.addHeader("Set-Cookie", "name3=value3; path=/test3; Secure;");
		resp.addHeader("Set-Cookie", "name4=value4; path=/test4; HttpOnly; Secure");
		
		resp.addHeader("Set-Cookie", "nameSecure=valueSecure; path=/Secure");
		
		resp.setHeader("Test","OK");
		HttpContext context = HttpObjectFactory.createHttpContext();
		filter.doFilter(req, resp, context);
		filter.afterResponse(req, resp, context);
		
		Header[] headers= resp.getHeaders("Set-Cookie");
		assertEquals("name1=value1; path=/test1; HttpOnly; SameSite=Strict", headers[0].getValue());
		assertEquals("name2=value2; path=/test2; HttpOnly; SameSite=Strict", headers[1].getValue());
		assertEquals("name3=value3; path=/test3; HttpOnly; SameSite=Strict", headers[2].getValue());
		assertEquals("name4=value4; path=/test4; HttpOnly; SameSite=Strict", headers[3].getValue());
		assertEquals("nameSecure=valueSecure; path=/Secure; HttpOnly; SameSite=Strict", headers[4].getValue());
	}
	
	/**
	 * HttpOnly=true, Secure=true
	 * httpSecureEnabled=false (default)
	 * X-Forwarded-Proto: https
	 */
	@Test
	public void testAfterResponseForwarededProto_AddSecure_HTTPS() {
		filter.setHttpOnly(true);
		filter.setSecure(true);
		filter.setUseForwardedProto(true);
		
		HttpRequest req = HttpObjectFactory.createHttpRequest("GET", "/");
		req.setHeader("X-Forwarded-Proto", "https"); //Internal HTTP Access.
		//
		HttpResponse resp = HttpObjectFactory.createHttpResponse(200, "OK");
		resp.addHeader("Set-Cookie", "name1=value1; path=/test1");
		resp.addHeader("Set-Cookie", "name2=value2; path=/test2; HttpOnly;");
		resp.addHeader("Set-Cookie", "name3=value3; path=/test3; Secure;");
		resp.addHeader("Set-Cookie", "name4=value4; path=/test4; HttpOnly; Secure");
		
		resp.addHeader("Set-Cookie", "nameSecure=valueSecure; path=/Secure");
		
		resp.setHeader("Test","OK");
		HttpContext context = HttpObjectFactory.createHttpContext();
		filter.doFilter(req, resp, context);
		filter.afterResponse(req, resp, context);
		
		Header[] headers= resp.getHeaders("Set-Cookie");
		assertEquals("name1=value1; path=/test1; HttpOnly; Secure; SameSite=Strict", headers[0].getValue());
		assertEquals("name2=value2; path=/test2; HttpOnly; Secure; SameSite=Strict", headers[1].getValue());
		assertEquals("name3=value3; path=/test3; HttpOnly; Secure; SameSite=Strict", headers[2].getValue());
		assertEquals("name4=value4; path=/test4; HttpOnly; Secure; SameSite=Strict", headers[3].getValue());
		assertEquals("nameSecure=valueSecure; path=/Secure; HttpOnly; Secure; SameSite=Strict", headers[4].getValue());
	}
	
	/**
	 * HttpOnly=true, Secure=true
	 * httpSecureEnabled=false (default)
	 * Empty X-Forwarded-Proto
	 */
	@Test
	public void testAfterResponseForwarededProto_Empty_AddSecure() {
		filter.setHttpOnly(true);
		filter.setSecure(true);
		filter.setUseForwardedProto(true);
		
		HttpRequest req = HttpObjectFactory.createHttpRequest("GET", "/");
		//req.setHeader("X-Forwarded-Proto", "https"); //Internal HTTP Access.
		//
		HttpResponse resp = HttpObjectFactory.createHttpResponse(200, "OK");
		resp.addHeader("Set-Cookie", "name1=value1; path=/test1");
		resp.addHeader("Set-Cookie", "name2=value2; path=/test2; HttpOnly;");
		resp.addHeader("Set-Cookie", "name3=value3; path=/test3; Secure;");
		resp.addHeader("Set-Cookie", "name4=value4; path=/test4; HttpOnly; Secure");
		
		resp.addHeader("Set-Cookie", "nameSecure=valueSecure; path=/Secure");
		
		resp.setHeader("Test","OK");
		HttpContext context = HttpObjectFactory.createHttpContext();
		filter.doFilter(req, resp, context);
		filter.afterResponse(req, resp, context);
		
		Header[] headers= resp.getHeaders("Set-Cookie");
		assertEquals("name1=value1; path=/test1; HttpOnly; Secure; SameSite=Strict", headers[0].getValue());
		assertEquals("name2=value2; path=/test2; HttpOnly; Secure; SameSite=Strict", headers[1].getValue());
		assertEquals("name3=value3; path=/test3; HttpOnly; Secure; SameSite=Strict", headers[2].getValue());
		assertEquals("name4=value4; path=/test4; HttpOnly; Secure; SameSite=Strict", headers[3].getValue());
		assertEquals("nameSecure=valueSecure; path=/Secure; HttpOnly; Secure; SameSite=Strict", headers[4].getValue());
	}
	
	/**
	 * HttpOnly=true, Secure=false
	 */
	@Test
	public void testAfterResponseAddSameSite() {
		filter.setHttpOnly(true);
		filter.setSecure(false);
		filter.setSameSite("Strict");
		filter.setCheckParentRequestHeader("X-Forwarded-For");
		filter.setHttpSecureEnabled(false);
		
		HttpRequest req = HttpObjectFactory.createHttpRequest("GET", "/");
		HttpResponse resp = HttpObjectFactory.createHttpResponse(200, "OK");
		resp.addHeader("Set-Cookie", "name1=value1; path=/test1");
		resp.addHeader("Set-Cookie", "name2=value2; path=/test2; SameSite=Lax;");
		resp.addHeader("Set-Cookie", "name3=value3; path=/test3; SameSite=Strict");
		resp.addHeader("Set-Cookie", "name4=value4; path=/test4; SameSite=");
		
		resp.addHeader("Set-Cookie", "nameSameSite=valueSameSite; path=/SameSite");
		
		resp.setHeader("Test","OK");
		HttpContext context = HttpObjectFactory.createHttpContext();
		filter.doFilter(req, resp, context);
		filter.afterResponse(req, resp, context);
		
		Header[] headers= resp.getHeaders("Set-Cookie");
		assertEquals("name1=value1; path=/test1; HttpOnly; SameSite=Strict", headers[0].getValue());
		assertEquals("name2=value2; path=/test2; HttpOnly; SameSite=Lax", headers[1].getValue());
		assertEquals("name3=value3; path=/test3; HttpOnly; SameSite=Strict", headers[2].getValue());
		assertEquals("name4=value4; path=/test4; HttpOnly; SameSite=", headers[3].getValue());
		assertEquals("nameSameSite=valueSameSite; path=/SameSite; HttpOnly; SameSite=Strict", headers[4].getValue());	
	}
}
