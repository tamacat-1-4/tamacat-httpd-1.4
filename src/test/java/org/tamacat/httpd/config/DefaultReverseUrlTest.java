/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config;

import static org.junit.Assert.*;

import java.net.URL;

import org.apache.http.HttpHost;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServiceType;

public class DefaultReverseUrlTest {

	ServiceUrl serviceUrl;
	ServerConfig config;
	DefaultReverseUrl reverseUrl;

	@Before
	public void setUp() throws Exception {
		config = new ServerConfig();
		serviceUrl = new ServiceUrl(config);
		serviceUrl.setPath("/test/");
		serviceUrl.setType(ServiceType.REVERSE);
		serviceUrl.setHost(new URL("http://localhost/test/"));
		reverseUrl = new DefaultReverseUrl(serviceUrl);
		reverseUrl.setReverse(new URL("http://localhost:8080/test2/"));
	}

	@Test
	public void testGetHost() {
		assertEquals("http://localhost", reverseUrl.getHost().toString());
	}

	@Test
	public void testGetPath() {
		assertEquals("/test/", reverseUrl.getServiceUrl().getPath());
	}

	@Test
	public void testGetReverse() {
		assertEquals(
			"http://localhost:8080/test2/",
			reverseUrl.getReverse().toString()
		);
	}

	@Test
	public void testGetReverseUrl() {
		assertEquals(
			"http://localhost:8080/test2/abc.html",
			reverseUrl.getReverseUrl("/test/abc.html").toString()
		);

		assertNull(reverseUrl.getReverseUrl(null));

		assertNull(reverseUrl.getReverseUrl("te://*@\\({}[]st test"));
	}

	@Test
	public void testGetTargetAddress() {
		assertEquals("localhost", reverseUrl.getTargetAddress().getHostName());
		assertEquals(8080, reverseUrl.getTargetAddress().getPort());
	}

	@Test
	public void testGetConvertRequestedUrl() throws Exception {
		serviceUrl.setHost(new URL("http://localhost"));
		assertEquals(
			"http://localhost/test/abc.html",
			reverseUrl.getConvertRequestedUrl("http://localhost:8080/test2/abc.html")
		);

		serviceUrl.setHost(new URL("http://localhost:10080"));
		assertEquals(
			"http://localhost:10080/test/abc.html",
			reverseUrl.getConvertRequestedUrl("http://localhost:8080/test2/abc.html")
		);
	}

	@Test
	public void testGetTargetHost() throws Exception {
		HttpHost host = reverseUrl.getTargetHost();
		assertEquals("http", host.getSchemeName());
		assertEquals("localhost", host.getHostName());
		assertEquals(8080, host.getPort());
	}

	@Test
	public void testClone() {
	}
}
