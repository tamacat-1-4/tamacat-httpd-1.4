/*
 * Copyright (c) 2010, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config.lb;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.DefaultReverseUrl;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.config.lb.LbRoundRobinServiceUrl;
import org.tamacat.httpd.config.lb.MonitorConfig;
import org.tamacat.httpd.exception.ServiceUnavailableException;

public class LbRoundRobinServiceUrlTest {

	ServerConfig serverConfig;
	
	@Before
	public void setUp() throws Exception {
		serverConfig = new ServerConfig();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLbRoundRobinServiceUrl() {
		new LbRoundRobinServiceUrl();
	}
	
	@Test
	public void testGetMonitorConfigDefault() throws Exception {
		LbRoundRobinServiceUrl url = new LbRoundRobinServiceUrl(serverConfig);
		ServiceUrl serviceUrl = new ServiceUrl(serverConfig);
		serviceUrl.setPath("/");
		DefaultReverseUrl reverseUrl = new DefaultReverseUrl(serviceUrl);
		reverseUrl.setReverse(new URL("http://localhost:8080/"));
		MonitorConfig config = url.getMonitorConfig(reverseUrl);
		assertNotNull(config);
		assertEquals("http://localhost:8080/check.html", config.getUrl());
		assertEquals(30000, config.getInterval());
		assertEquals(10000, config.getTimeout());
	}
	
	@Test
	public void testGetReverseUrls() throws Exception {
		LbRoundRobinServiceUrl url = new LbRoundRobinServiceUrl(serverConfig);
		ServiceUrl serviceUrl = new ServiceUrl(serverConfig);
		DefaultReverseUrl reverseUrl = new DefaultReverseUrl(serviceUrl);
		url.setReverseUrl(reverseUrl);
		assertEquals(1, url.getReverseUrls().size());
	}
	
	@Test
	public void testGetReverseUrl() throws Exception {
		LbRoundRobinServiceUrl url = new LbRoundRobinServiceUrl(serverConfig);
		try {
			url.getReverseUrl();
			fail();
		} catch (ServiceUnavailableException e) {
		}

		ServiceUrl serviceUrl = new ServiceUrl(serverConfig);
		DefaultReverseUrl reverseUrl = new DefaultReverseUrl(serviceUrl);
		reverseUrl.setReverse(new URL("http://192.168.1.11:8080/test/"));
		url.setReverseUrl(reverseUrl);
		assertNotNull(url.getReverseUrl());
		
		DefaultReverseUrl reverseUrl2 = new DefaultReverseUrl(serviceUrl);
		reverseUrl2.setReverse(new URL("http://192.168.1.12:8080/test/"));
		url.setReverseUrl(reverseUrl2);

		assertEquals("http://192.168.1.11:8080/test/", url.getReverseUrl().getReverse().toString());
		assertEquals("http://192.168.1.12:8080/test/", url.getReverseUrl().getReverse().toString());
		assertEquals("http://192.168.1.11:8080/test/", url.getReverseUrl().getReverse().toString());
		assertEquals("http://192.168.1.12:8080/test/", url.getReverseUrl().getReverse().toString());
	}
	
	@Test
	public void testAddTarget() throws Exception {
		LbRoundRobinServiceUrl url = new LbRoundRobinServiceUrl(serverConfig);
		try {
			url.getReverseUrl();
			fail();
		} catch (ServiceUnavailableException e) {
			//OK
		}

		ServiceUrl serviceUrl = new ServiceUrl(serverConfig);
		DefaultReverseUrl reverseUrl = new DefaultReverseUrl(serviceUrl);
		reverseUrl.setReverse(new URL("http://192.168.1.11:8080/test/"));
		url.addTarget(reverseUrl);
		assertNotNull(url.getReverseUrl());
		
		DefaultReverseUrl reverseUrl2 = new DefaultReverseUrl(serviceUrl);
		reverseUrl2.setReverse(new URL("http://192.168.1.12:8080/test/"));
		url.addTarget(reverseUrl2);

		assertEquals("http://192.168.1.11:8080/test/", url.getReverseUrl().getReverse().toString());
		assertEquals("http://192.168.1.12:8080/test/", url.getReverseUrl().getReverse().toString());
		assertEquals("http://192.168.1.11:8080/test/", url.getReverseUrl().getReverse().toString());
		assertEquals("http://192.168.1.12:8080/test/", url.getReverseUrl().getReverse().toString());
		
		url.removeTarget(reverseUrl);
		assertEquals("http://192.168.1.12:8080/test/", url.getReverseUrl().getReverse().toString());
		assertEquals("http://192.168.1.12:8080/test/", url.getReverseUrl().getReverse().toString());
	}
	
	@Test
	public void testGetMonitorConfig() throws Exception {
		LbRoundRobinServiceUrl url = new LbRoundRobinServiceUrl(serverConfig);
		ServiceUrl serviceUrl = new ServiceUrl(serverConfig);
		DefaultReverseUrl reverseUrl = new DefaultReverseUrl(serviceUrl);
		serviceUrl.setPath("/lb/");

		reverseUrl.setReverse(new URL("http://localhost:8080/lb1/"));
		MonitorConfig config = url.getMonitorConfig(reverseUrl);
		assertNotNull(config);
		assertEquals("http://localhost:8080/lb1/test/check.html", config.getUrl());
		assertEquals(60000, config.getInterval());
		assertEquals(15000, config.getTimeout());
	}
	
	@Test
	public void testStartHealthCheck() throws Exception {
		LbRoundRobinServiceUrl url = new LbRoundRobinServiceUrl(serverConfig);
		url.startHealthCheck();
		
		ServiceUrl serviceUrl = new ServiceUrl(serverConfig);
		DefaultReverseUrl reverseUrl = new DefaultReverseUrl(serviceUrl);
		reverseUrl.setReverse(new URL("http://192.168.1.11:8080/test/"));
		url.addTarget(reverseUrl);
		
		url.startHealthCheck();
	}
}
