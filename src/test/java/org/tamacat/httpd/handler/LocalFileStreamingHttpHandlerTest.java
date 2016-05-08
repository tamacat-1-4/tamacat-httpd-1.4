package org.tamacat.httpd.handler;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.mock.HttpObjectFactory;

public class LocalFileStreamingHttpHandlerTest {

	HttpRequest request = HttpObjectFactory.createHttpRequest("GET", "/");
	HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
	HttpContext context = HttpObjectFactory.createHttpContext();
	LocalFileStreamingHttpHandler handler;

	@Before
	public void setUp() throws Exception {
		handler = new LocalFileStreamingHttpHandler();

		ServiceUrl serviceUrl = new ServiceUrl();
		serviceUrl.setPath("/");
		handler.setServiceUrl(serviceUrl);
		handler.setListings(true);
		handler.setDocsRoot("./src/test/resources/htdocs/root/");
		handler.doRequest(request, response, context);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDoRequest() throws Exception {
		handler.doRequest(request, response, context);
	}

	@Test
	public void testSetAcceptRanges() {
		handler.setAcceptRanges(true);
		assertTrue(handler.acceptRanges);
	}

	@Test
	public void testSetBufferSize() {
		handler.setBufferSize(8192);
	}

	@Test
	public void testPartialContent() {
		handler.partialContent(response, new File("./src/test/resources/htdocs/root/index.vm"), 0, 10);
	}

}
