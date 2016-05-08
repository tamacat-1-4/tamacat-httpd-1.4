package org.tamacat.httpd.core;

import static org.junit.Assert.*;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.mock.HttpObjectFactory;

public class KeepAliveConnReuseStrategyTest {

	KeepAliveConnReuseStrategy reuse;
	HttpResponse response;
	HttpContext context = new BasicHttpContext();

	@Before
	public void setUp() throws Exception {
		reuse = new KeepAliveConnReuseStrategy();
		response = HttpObjectFactory.createHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetKeepAliveTimeout() {
		reuse.setKeepAliveTimeout(0);
		assertEquals(0, reuse.keepAliveTimeout);
	}

	@Test
	public void testSetMaxKeepAliveRequests() {
		reuse.setMaxKeepAliveRequests(0);

	}

	@Test
	public void testSetDisabledKeepAlive() {
		reuse.setDisabledKeepAlive(true);
		assertFalse(reuse.keepAlive(response, context));
	}

	@Test
	public void testKeepAliveHttpResponseHttpContext() {

	}

	@Test
	public void testKeepAliveCheck_HTTP_1_1() {
		assertTrue(reuse.keepAliveCheck(response, context));

		response.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
		assertFalse(reuse.keepAliveCheck(response, context));
	}

	@Test
	public void testKeepAliveCheck_HTTP_1_0() {
		response = HttpObjectFactory.createHttpResponse(HttpVersion.HTTP_1_0, 200, "OK");

		response.setHeader(HTTP.CONTENT_LEN, "123");
		assertFalse(reuse.keepAliveCheck(response, context));

		response.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
		assertFalse(reuse.keepAliveCheck(response, context));

		response.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
		assertTrue(reuse.keepAliveCheck(response, context));
	}

	@Test
	public void testKeepAliveCheck_TransferEncoding() {
		response = HttpObjectFactory.createHttpResponse(HttpVersion.HTTP_1_0, 200, "OK");

		response.setHeader(HTTP.TRANSFER_ENCODING, "none");
		response.removeHeaders(HTTP.CONTENT_LEN);
		response.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
		assertFalse(reuse.keepAliveCheck(response, context));

		response.setHeader(HTTP.TRANSFER_ENCODING, "chunked");
		response.removeHeaders(HTTP.CONTENT_LEN);
		response.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
		assertTrue(reuse.keepAliveCheck(response, context));
	}


	@Test
	public void testIsKeepAliveTimeout() throws Exception {
		assertFalse(reuse.isKeepAliveTimeout(context));

		context.setAttribute(KeepAliveConnReuseStrategy.HTTP_IN_CONN, new Object());
		assertFalse(reuse.isKeepAliveTimeout(context));

		ServerHttpConnection conn = new ServerHttpConnection(8192);
		context.setAttribute(KeepAliveConnReuseStrategy.HTTP_IN_CONN, conn);
		assertFalse(reuse.isKeepAliveTimeout(context));

		reuse.setKeepAliveTimeout(1);
		Thread.sleep(100);
		assertTrue(reuse.isKeepAliveTimeout(context));

		reuse.setKeepAliveTimeout(5000);
		reuse.setMaxKeepAliveRequests(0);
		assertTrue(reuse.isKeepAliveTimeout(context));
	}

	@Test
	public void testCanResponseHaveBody() {
		response.setStatusCode(200);
		assertTrue(reuse.canResponseHaveBody(response));

		response.setStatusCode(204);
		assertFalse(reuse.canResponseHaveBody(response));

		response.setStatusCode(304);
		assertFalse(reuse.canResponseHaveBody(response));

		response.setStatusCode(205);
		assertFalse(reuse.canResponseHaveBody(response));

		response.setStatusCode(404);
		assertTrue(reuse.canResponseHaveBody(response));
	}

}
