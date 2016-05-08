/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core.ssl;

import static org.junit.Assert.*;

import java.util.Properties;

import javax.net.ssl.SSLContext;

import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.core.ssl.DefaultSSLContextCreator;

public class SSLContextCreatorTest {
	
	@Test
	public void testSSLContextCreatorServerConfig() throws Exception {
		ServerConfig config = new ServerConfig(new Properties());
		config.setParam("https.keyStoreFile", "test.keystore");
		config.setParam("https.keyPassword", "nopassword");
		config.setParam("https.keyStoreType", "JKS");
		config.setParam("https.protocol", "TLS");
		
		DefaultSSLContextCreator creator = new DefaultSSLContextCreator(config);
		SSLContext ctx = creator.getSSLContext();
		assertNotNull(ctx);
	}

	@Test
	public void testGetSSLContext() throws Exception {
		DefaultSSLContextCreator creator = new DefaultSSLContextCreator();
		creator.setKeyStoreFile("test.keystore");
		creator.setKeyPassword("nopassword");
		creator.setKeyStoreType("JKS");
		creator.setSSLProtocol("TLS");
		
		SSLContext ctx = creator.getSSLContext();
		assertNotNull(ctx);
	}
}
