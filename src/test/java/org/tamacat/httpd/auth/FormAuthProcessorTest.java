/*
 * Copyright (c) 2010, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import static org.junit.Assert.*;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.core.BasicHttpStatus;
import org.tamacat.httpd.exception.UnauthorizedException;
import org.tamacat.httpd.mock.HttpObjectFactory;
import org.tamacat.httpd.util.RequestUtils;

public class FormAuthProcessorTest {

	ServerConfig config;
	FormAuthProcessor auth;
	HttpRequest request;
	HttpResponse response;
	HttpContext context;
	TestAuthComponent authComponent;
	
	@Before
	public void setUp() throws Exception {
		config = new ServerConfig();
		ServiceUrl serviceUrl = new ServiceUrl(config);
		auth = new FormAuthProcessor();
		auth.init(serviceUrl);

		authComponent = new TestAuthComponent();
		authComponent.setAuthUsername("admin");
		authComponent.setAuthPassword("pass");
		auth.setAuthComponent(authComponent);
		request = HttpObjectFactory.createHttpRequest("GET", "/");
		response = HttpObjectFactory.createHttpResponse(200, "OK");
		context = new BasicHttpContext();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIsFreeAccessExtensions() {
		auth.setFreeAccessExtensions(".css, .js, .gif");
		
		assertTrue(auth.isFreeAccess("/test/test.css"));
		assertTrue(auth.isFreeAccess("/test/test.js"));
		assertTrue(auth.isFreeAccess("/test/test.gif"));
		
		assertFalse(auth.isFreeAccess("/test/"));
		assertFalse(auth.isFreeAccess("."));
		assertFalse(auth.isFreeAccess("/"));
		assertFalse(auth.isFreeAccess(""));
		assertFalse(auth.isFreeAccess("/test/test.png"));
		assertFalse(auth.isFreeAccess("/test/test.jsp"));
	}

	@Test
	public void testSendRedirect() throws Exception {
		auth.sendRedirect(request, response, "/test/login.html");
		assertEquals(
			"<html><meta http-equiv=\"refresh\" content=\"0;"
			  + "url=/test/login.html\"></html>",
			EntityUtils.toString(response.getEntity())
		);
	}

	@Test
	public void testCheckUser() throws Exception {
		try {
			context = new BasicHttpContext();
			auth.checkUser(request, response, context);
			fail();
		} catch (UnauthorizedException e) {
			assertEquals(BasicHttpStatus.SC_UNAUTHORIZED, e.getHttpStatus());
		}
		
		//login (NG)
		try {
			context = new BasicHttpContext();
			RequestUtils.parseParameters(request, context, "UTF-8")
				.addParam("username", "").addParam("password", "");
			
			auth.checkUser(request, response, context);
			fail();
		} catch (UnauthorizedException e) {
			assertEquals(BasicHttpStatus.SC_UNAUTHORIZED, e.getHttpStatus());
		}

		//login (NG)
		try {
			context = new BasicHttpContext();
			RequestUtils.parseParameters(request, context, "UTF-8")
				.addParam("username", "admin").addParam("password", "xxxx");
			auth.checkUser(request, response, context);
			fail();
		} catch (UnauthorizedException e) {
			assertEquals(BasicHttpStatus.SC_UNAUTHORIZED, e.getHttpStatus());
		}
		
		//login (NG)
		try {
			context = new BasicHttpContext();
			RequestUtils.parseParameters(request, context, "UTF-8")
				.addParam("username", "test").addParam("password", "pass");
			auth.checkUser(request, response, context);
			fail();
		} catch (UnauthorizedException e) {
			assertEquals(BasicHttpStatus.SC_UNAUTHORIZED, e.getHttpStatus());
		}
		
		//login
		context = new BasicHttpContext();
		RequestUtils.parseParameters(request, context, "UTF-8")
			.addParam("username", "admin").addParam("password", "pass");
		auth.checkUser(request, response, context);
	}

	@Test
	public void testGetLoginPageUrlWithRedirect() {
		request = HttpObjectFactory.createHttpRequest("GET", "/test/");
		assertEquals("login.html?redirect=%2Ftest%2F", auth.getLoginPageUrlWithRedirect(request));
		
		request = HttpObjectFactory.createHttpRequest("GET", "/check.html");
		assertEquals("login.html", auth.getLoginPageUrlWithRedirect(request));
		
		request = HttpObjectFactory.createHttpRequest("GET", "/logout.html");
		assertEquals("login.html", auth.getLoginPageUrlWithRedirect(request));
	}
	
	@Test
	public void testGetEncryptedPassword() {
		String password = auth.getEncryptedPassword("password");
		assertEquals("password", password);
		
		auth.setPasswordEncryptionAlgorithm("SHA-256");
		String encpassword = auth.getEncryptedPassword("password");
		assertEquals("5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8", encpassword);
		//System.out.println(encpassword);
	}
}
