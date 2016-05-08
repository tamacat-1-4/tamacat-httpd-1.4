package org.tamacat.httpd.handler;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceConfig;
import org.tamacat.httpd.config.ServiceType;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.handler.DefaultHttpHandlerFactory;
import org.tamacat.httpd.handler.HttpHandler;
import org.tamacat.httpd.handler.VelocityHttpHandler;

public class DefaultHttpHandlerFactoryTest {

	ServerConfig serverConfig;
	ServiceConfig serviceConfig;
	DefaultHttpHandlerFactory factory;
	ServiceUrl serviceUrl;
	
	@Before
	public void setUp() throws Exception {
		serverConfig = new ServerConfig();
		serviceConfig = new ServiceConfig();
		serviceUrl = new ServiceUrl(serverConfig);
		serviceUrl.setHandlerName("VelocityHandler");
		serviceUrl.setPath("/");
		serviceUrl.setType(ServiceType.NORMAL);
		serviceConfig.addServiceUrl(serviceUrl);
		
		factory = new DefaultHttpHandlerFactory("components.xml");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetHttpHandler() {
		HttpHandler handler = factory.getHttpHandler(serviceUrl);
		assertEquals(false, ((VelocityHttpHandler)handler).listings);
		
		handler = factory.getHttpHandler(serviceUrl);
		assertEquals(false, ((VelocityHttpHandler)handler).listings);
	}
}
