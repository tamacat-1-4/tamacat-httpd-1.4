package org.tamacat.httpd.handler;

import static org.junit.Assert.*;

import java.net.URL;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.DefaultReverseUrl;
import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.mock.HttpObjectFactory;

public class ReverseHttpRequestFactoryTest {

	ServiceUrl serviceUrl;
	ReverseUrl reverseUrl;

	@Before
	public void setUp() throws Exception {
		ServerConfig config = new ServerConfig();
		serviceUrl = new ServiceUrl(config);
		serviceUrl.setPath("/");
		reverseUrl = new DefaultReverseUrl(serviceUrl);
		reverseUrl.setReverse(new URL("http://localhost:8080/"));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetInstanceGET() {
		HttpRequest request = HttpObjectFactory.createHttpRequest("GET", "/");
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
		HttpContext context = HttpObjectFactory.createHttpContext();

		assertFalse(
			ReverseHttpRequestFactory.getInstance(request, response, context, reverseUrl)
			instanceof ReverseHttpEntityEnclosingRequest);

	}

	@Test
	public void testGetInstancePOST() {
		HttpRequest request = HttpObjectFactory.createHttpRequest("POST", "/");
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
		HttpContext context = HttpObjectFactory.createHttpContext();

		assertTrue(
			ReverseHttpRequestFactory.getInstance(request, response, context, reverseUrl)
			instanceof ReverseHttpEntityEnclosingRequest);
	}

}
