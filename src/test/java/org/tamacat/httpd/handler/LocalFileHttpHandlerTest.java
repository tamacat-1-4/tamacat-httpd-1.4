package org.tamacat.httpd.handler;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.httpd.exception.NotFoundException;
import org.tamacat.httpd.filter.AccessLogFilter;
import org.tamacat.httpd.handler.LocalFileHttpHandler;
import org.tamacat.httpd.mock.HttpObjectFactory;

public class LocalFileHttpHandlerTest {

	@Test
	public void testHandle() {
		LocalFileHttpHandler handler = new LocalFileHttpHandler();
		HttpRequest request = HttpObjectFactory.createHttpRequest("GET", "/");
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
		HttpContext context = HttpObjectFactory.createHttpContext();

		ServiceUrl serviceUrl = new ServiceUrl();
		serviceUrl.setPath("/");
		handler.setServiceUrl(serviceUrl);
		handler.setListings(true);
		handler.setDocsRoot("./src/test/resources/htdocs/root/");

		handler.setHttpFilter(new AccessLogFilter());
		handler.handle(request, response, context);
	}

	@Test
	public void testDoRequest() throws Exception {
		HttpRequest request = HttpObjectFactory.createHttpRequest("GET", "/");
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
		HttpContext context = HttpObjectFactory.createHttpContext();

		LocalFileHttpHandler handler = new LocalFileHttpHandler();
		ServiceUrl serviceUrl = new ServiceUrl();
		serviceUrl.setPath("/");
		handler.setServiceUrl(serviceUrl);
		handler.setListings(true);
		handler.setDocsRoot("./src/test/resources/htdocs/root/");
		handler.doRequest(request, response, context);
	}

	@Test
	public void testSetWelcomeFile() {
		LocalFileHttpHandler handler = new LocalFileHttpHandler();
		assertNotNull(handler.welcomeFile);
		assertEquals("index.html", handler.welcomeFile);

		handler.setWelcomeFile("top.html");
		assertEquals("top.html", handler.welcomeFile);

		handler.setListings(false);
		assertEquals("top.html", handler.welcomeFile);

		handler.setListings(true);
		assertNull(handler.welcomeFile);
	}

	@Test
	public void testSetListingPages() {
		LocalFileHttpHandler handler = new LocalFileHttpHandler();
		handler.setServiceUrl(new ServiceUrl());
		assertNotNull(handler.listingPage);

		handler.setListingsPage("");
		assertNotNull(handler.listingPage);

	}

	@Test
	public void testSetEncoding() {
		LocalFileHttpHandler handler = new LocalFileHttpHandler();
		assertEquals("UTF-8", handler.encoding);

		handler.setEncoding("Windows-31J");
		assertEquals("Windows-31J", handler.encoding);
	}

	@Test
	public void testGetDecodeUri() {
		LocalFileHttpHandler handler = new LocalFileHttpHandler();
		assertEquals("/", handler.getDecodeUri("/"));
		assertEquals("/test/index.html", handler.getDecodeUri("/test/index.html"));
		assertEquals("///", handler.getDecodeUri("///"));

		assertEquals("/\\index.html", handler.getDecodeUri("/\\index.html"));

		assertEquals("/ index.html", handler.getDecodeUri("/%20index.html"));
		assertEquals("/..", handler.getDecodeUri("/%2e%2e"));
		assertEquals("/.", handler.getDecodeUri("/%2e"));
		assertEquals("/./index.html", handler.getDecodeUri("/%2e/index.html"));
		assertEquals("///index.html", handler.getDecodeUri("/%2f/index.html"));

		try {
			handler.getDecodeUri("/%2e%2e/index.html");
			fail();
		} catch (NotFoundException e) {
			assertTrue(true);
		}
		try {
			handler.getDecodeUri("/%2e%2e%2findex.html");
			fail();
		} catch (NotFoundException e) {
			assertTrue(true);
		}
		try {
			handler.getDecodeUri("/../");
			fail();
		} catch (NotFoundException e) {
			assertTrue(true);
		}
		try {
			handler.getDecodeUri("../");
			fail();
		} catch (NotFoundException e) {
			assertTrue(true);
		}

		try {
			handler.getDecodeUri("..\\index.html");
			fail();
		} catch (NotFoundException e) {
			assertTrue(true);
		}

		handler.setEncoding("none");
		assertEquals("/index.html", handler.getDecodeUri("/index.html"));
		assertEquals("/%20index.html", handler.getDecodeUri("/%20index.html"));
		try {
			assertEquals("/../index.html", handler.getDecodeUri("/../index.html"));
			fail();
		} catch (NotFoundException e) {
		}
	}

	@Test
	public void testGetFileEntity() {
		LocalFileHttpHandler handler = new LocalFileHttpHandler();
		assertNotNull(handler.getFileEntity(new File("./src/test/resources/htdocs/index.html")));
	}

	@Test
	public void testSetClassLoader() {
		LocalFileHttpHandler handler = new LocalFileHttpHandler();
		assertEquals(getClass().getClassLoader(), handler.getClassLoader());

		handler.setClassLoader(Thread.currentThread().getContextClassLoader());
		assertEquals(Thread.currentThread().getContextClassLoader(), handler.getClassLoader());
	}
	
	@Test
	public void testDefaultAllowMethods() {
		LocalFileHttpHandler handler = new LocalFileHttpHandler();
		assertEquals("GET,HEAD,POST,OPTIONS", handler.allowMethodValue);
		
		HttpRequest request = HttpObjectFactory.createHttpRequest("OPTIONS", "/");
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
		HttpContext context = HttpObjectFactory.createHttpContext();
		handler.handle(request, response, context);
		assertEquals("GET,HEAD,POST,OPTIONS", response.getFirstHeader("Allow").getValue());
	}
	
	@Test
	public void testSetAllowMethods() {
		LocalFileHttpHandler handler = new LocalFileHttpHandler();
		handler.setAllowMethods("GET,HEAD,POST");
		assertEquals("GET,HEAD,POST", handler.allowMethodValue);
		
		HttpRequest request = HttpObjectFactory.createHttpRequest("OPTIONS", "/");
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
		HttpContext context = HttpObjectFactory.createHttpContext();
		try {
			handler.handle(request, response, context);
			fail();
		} catch (HttpException e) {
			assertTrue(e.getHttpStatus().getStatusCode() == 405);
		}
	}
	
	@Test
	public void testSetAllowMethodsNull() {
		LocalFileHttpHandler handler = new LocalFileHttpHandler();
		ServiceUrl serviceUrl = new ServiceUrl();
		serviceUrl.setPath("/");
		handler.setServiceUrl(serviceUrl);
		handler.setListings(true);
		handler.setDocsRoot("./src/test/resources/htdocs/root/");
		
		handler.setAllowMethods(null);
		assertNull(handler.allowMethodValue);
		
		HttpRequest request = HttpObjectFactory.createHttpRequest("OPTIONS", "/");
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
		HttpContext context = HttpObjectFactory.createHttpContext();
		try {
			handler.handle(request, response, context);
			assertTrue(response.getStatusLine().getStatusCode() == 200);
		} catch (HttpException e) {
			fail();
		}
	}
}
