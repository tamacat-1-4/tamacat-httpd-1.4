package org.tamacat.httpd.handler;

import static org.junit.Assert.*;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.core.HttpProcessorBuilder;
import org.tamacat.httpd.core.KeepAliveConnReuseStrategy;
import org.tamacat.httpd.core.ServerHttpConnection;
import org.tamacat.httpd.exception.NotFoundException;
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.httpd.handler.DefaultHttpService;
import org.tamacat.httpd.mock.DummySocket;
import org.tamacat.httpd.mock.HttpObjectFactory;

public class DefaultHttpServiceTest {

	DefaultHttpService service;
	@Before
	public void setUp() throws Exception {
		service = new DefaultHttpService(
			new HttpProcessorBuilder(),
			new KeepAliveConnReuseStrategy(),
			new DefaultHttpResponseFactory(), null, null);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHandleRequestHttpServerConnectionHttpContext() throws Exception {
		ServerHttpConnection conn = new ServerHttpConnection(8192);
		conn.bind(new DummySocket());
		HttpContext context = new BasicHttpContext();
		service.handleRequest(conn, context);
	}

	@Test
	public void testDoServiceHttpRequestHttpResponseHttpContext() throws Exception {
		HttpRequest request = new BasicHttpRequest("GET", "/");
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
		HttpContext context = new BasicHttpContext();
		service.doService(request, response, context);
	}

	@Test
	public void testHandleExceptionHttpRequestHttpResponseHttpException() {
		HttpRequest request = new BasicHttpRequest("GET", "/");
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
		service.handleException(request, response, new ServiceUnavailableException());
	}

	@Test
	public void testGetErrorPage() {
		HttpRequest request = new BasicHttpRequest("GET", "/");
		HttpResponse response = HttpObjectFactory.createHttpResponse(404, "Not Found");
		assertTrue(service.getErrorPage()
			.getErrorPage(request, response, new NotFoundException())
			.indexOf("404 Not Found")>=0
		);
	}

	@Test
	public void testGetEntity() {
		service.setEncoding("UTF-8");
		assertNotNull(service.getEntity("<html />"));

		service.setEncoding("none");
		assertNull(service.getEntity("<html />"));
	}

	@Test
	public void testGetClassLoader() {
	}

}
