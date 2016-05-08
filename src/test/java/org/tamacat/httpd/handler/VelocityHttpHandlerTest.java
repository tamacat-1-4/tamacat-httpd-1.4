package org.tamacat.httpd.handler;

import static org.junit.Assert.*;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.NotFoundException;
import org.tamacat.httpd.handler.VelocityHttpHandler;
import org.tamacat.httpd.mock.HttpObjectFactory;

public class VelocityHttpHandlerTest {

	HttpRequest request = HttpObjectFactory.createHttpRequest("GET", "/");
	HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
	HttpContext context = HttpObjectFactory.createHttpContext();
	VelocityHttpHandler handler;

	@Before
	public void setUp() throws Exception {
		handler = new VelocityHttpHandler();

		ServerConfig config = new ServerConfig();
		ServiceUrl serviceUrl = new ServiceUrl(config);
		serviceUrl.setPath("/");
		handler.setDocsRoot("./src/test/resources/htdocs/root/");
		handler.setListings(true);
		handler.setServiceUrl(serviceUrl);
	}

	@Test
	public void testDoRequest() throws Exception {
		handler.doRequest(request, response, context);
		assertTrue(
			handler.errorPage.getErrorPage(request, response, new NotFoundException()
		).indexOf("404 Not Found")>=0);
	}


	@Test
	public void testIsMatchUrlPattern_default() {
		//VelocityHttpHandler handler = new VelocityHttpHandler();
		assertTrue(handler.isMatchUrlPattern("/test.html"));
		assertTrue(handler.isMatchUrlPattern("/ctl/test.html"));
		assertFalse(handler.isMatchUrlPattern("/ctl/"));
	}

	@Test
	public void testIsMatchUrlPattern_single() {
		//VelocityHttpHandler handler = new VelocityHttpHandler();
		handler.setUrlPatterns(".do");
		assertFalse(handler.isMatchUrlPattern("/test.html"));
		assertFalse(handler.isMatchUrlPattern("/ctl/test.html"));
		assertFalse(handler.isMatchUrlPattern("/ctl/"));
		assertTrue(handler.isMatchUrlPattern("/test.do"));
	}

	@Test
	public void testIsMatchUrlPattern_multi() {
		//VelocityHttpHandler handler = new VelocityHttpHandler();
		handler.setUrlPatterns("/ctl/, .do");
		assertFalse(handler.isMatchUrlPattern("/test.html"));
		assertTrue(handler.isMatchUrlPattern("/ctl/test.html"));
		assertTrue(handler.isMatchUrlPattern("/ctl/"));
		assertTrue(handler.isMatchUrlPattern("/test.do"));
	}
	
	@Test
	public void testSetFileEntity() {
		try {
			handler.setFileEntity(request, response, "/docs");
			fail();
		} catch (NotFoundException e) {
			assertEquals("/docs is not found this server.", e.getMessage());
		}
	}
}
