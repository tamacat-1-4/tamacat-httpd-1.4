package org.tamacat.httpd.auth;

import static org.junit.Assert.*;

import java.util.Base64;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.UnauthorizedException;
import org.tamacat.httpd.mock.HttpObjectFactory;

public class BasicAuthProcessorTest {

	ServerConfig config;
	BasicAuthProcessor auth;
	HttpRequest request;
	HttpResponse response;
	HttpContext context;

	@Before
	public void setUp() throws Exception {
		config = new ServerConfig();
		ServiceUrl serviceUrl = new ServiceUrl(config);
		auth = new BasicAuthProcessor();
		auth.init(serviceUrl);

		TestAuthComponent authComponent = new TestAuthComponent();
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
	public void testDoFilter() {
		try {
			auth.doFilter(request, response, context);
		} catch (UnauthorizedException e) {
			assertTrue(true);
		}

		String idpass = new String(Base64.getEncoder().encode("admin:pass".getBytes()));
		request.setHeader(BasicAuthProcessor.AUTHORIZATION, "Basic " + idpass);
		try {
			auth.doFilter(request, response, context);
		} catch (UnauthorizedException e) {
			fail();
		}
	}

	@Test
	public void testDoFilter_OPTIONS() {
		request = HttpObjectFactory.createHttpRequest("OPTIONS", "/");
		try {
			auth.doFilter(request, response, context);
			assertTrue(HttpStatus.SC_NO_CONTENT == response.getStatusLine().getStatusCode());
		} catch (UnauthorizedException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testCheckUser() {
		try {
			String id = auth.checkUser(request, response, context);
			assertNull(id);
		} catch (UnauthorizedException e) {
			assertTrue(true);
		}
		try {
			String idpass = new String(Base64.getEncoder().encode("admin:pass".getBytes()));
			request.setHeader(BasicAuthProcessor.AUTHORIZATION, "Basic " + idpass);
			String id = auth.checkUser(request, response, context);
			assertNotNull(id);
			assertEquals("admin", id);
		} catch (UnauthorizedException e) {
			fail();
		}
	}
}
