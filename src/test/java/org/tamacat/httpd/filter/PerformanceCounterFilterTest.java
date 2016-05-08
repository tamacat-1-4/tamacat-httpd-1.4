package org.tamacat.httpd.filter;

import static org.junit.Assert.*;

import java.net.URL;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.mock.HttpObjectFactory;

public class PerformanceCounterFilterTest {

	PerformanceCounterFilter filter;
	ServerConfig config;
	ServiceUrl serviceUrl;


	@Before
	public void setUp() throws Exception {
		config = new ServerConfig();
		serviceUrl = new ServiceUrl(config);
		serviceUrl.setHost(new URL("http://localhost/"));
		serviceUrl.setPath("/");
		filter = new PerformanceCounterFilter();
		filter.setObjectName("org.tamacat.httpd:type=URL#");
		filter.init(serviceUrl);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDoFilter() {
		HttpRequest request = HttpObjectFactory.createHttpRequest("GET", "/");
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
		filter.doFilter(request, response, HttpObjectFactory.createHttpContext());
		assertTrue(true);
	}

	@Test
	public void testAfterResponse() {
		HttpRequest request = HttpObjectFactory.createHttpRequest("GET", "/");
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");
		filter.afterResponse(request, response, HttpObjectFactory.createHttpContext());
		assertTrue(true);
	}

	@Test
	public void testGetPath() throws Exception {
		ServiceUrl serviceUrl = new ServiceUrl(config);
		serviceUrl.setHost(new URL("http://localhost/"));
		serviceUrl.setPath("/");
		assertEquals("localhost/", filter.getPath(serviceUrl));

		serviceUrl = new ServiceUrl(config);
		serviceUrl.setHost(new URL("http://localhost"));
		serviceUrl.setPath("/");
		assertEquals("localhost/", filter.getPath(serviceUrl));

		serviceUrl = new ServiceUrl(config);
		serviceUrl.setHost(new URL("http://localhost:8080/"));
		serviceUrl.setPath("/");
		assertEquals("localhost_8080/", filter.getPath(serviceUrl));

		serviceUrl = new ServiceUrl(config);
		serviceUrl.setHost(new URL("http://www.example.com:8080/"));
		serviceUrl.setPath("/test/");
		assertEquals("www.example.com_8080/test/", filter.getPath(serviceUrl));
	}
}
