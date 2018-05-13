/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({System.class, ServiceConfigParser.class})
public class ServiceConfigParserTest {

	ServiceConfigParser parser;

	@Before
	public void setUp() throws Exception {
		ServerConfig serverConfig = new ServerConfig();
		serverConfig.setParam("url-config.file", "url-config.xml");
		parser = new ServiceConfigParser(serverConfig);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetServiceConfig() {
		HostServiceConfig config = parser.getConfig();
		ServiceConfig serviceConfig = config.getDefaultServiceConfig();
		List<ServiceUrl> list = serviceConfig.getServiceUrlList();
		Assert.assertTrue(list.size() > 0);
	}
	
	@Test
	public void testreplaceEnvironmentVariable() {
		PowerMockito.mockStatic(System.class);
		PowerMockito.when(System.getenv("LOCAL_SERVER")).thenReturn("localhost");
		PowerMockito.when(System.getenv("LOCAL_PORT")).thenReturn("8080");
		assertEquals(
			"http://localhost:8080/examples/", 
			ServiceConfigParser.replaceEnvironmentVariable("http://${LOCAL_SERVER}:${LOCAL_PORT}/examples/")
		);
	}
}
