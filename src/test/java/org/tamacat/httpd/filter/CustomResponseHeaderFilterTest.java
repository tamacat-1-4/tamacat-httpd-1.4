package org.tamacat.httpd.filter;

import static org.junit.Assert.*;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;
import org.tamacat.httpd.mock.HttpObjectFactory;
import org.tamacat.httpd.util.HeaderUtils;

public class CustomResponseHeaderFilterTest {
	
	HttpContext context = HttpObjectFactory.createHttpContext();
	HttpRequest request = HttpObjectFactory.createHttpRequest("GET", "/test/");
	HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
	
	@Test
	public void testSetAppendHeader() {
		CustomResponseHeaderFilter filter = new CustomResponseHeaderFilter();
		filter.setAppendHeader("Connection", "Keep-Alive");
		filter.afterResponse(request, response, context);
		assertEquals("Keep-Alive", HeaderUtils.getHeader(response, "Connection"));
	}

	@Test
	public void testSetRemoveHeader() {
		response.setHeader("Connection", "Keep-Alive");
		CustomResponseHeaderFilter filter = new CustomResponseHeaderFilter();
		filter.setRemoveHeader("Connection");
		filter.afterResponse(request, response, context);
		assertEquals(null, response.getFirstHeader("Connection"));
	}
}
