/*
 * Copyright (c) 2015 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core.ssl;

import static org.junit.Assert.*;

import java.util.Properties;

import javax.net.ssl.SSLContext;

import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class SSLSNIContextCreatorTest {
	
	static final Log LOG = LogFactory.getLog(SSLSNIContextCreatorTest.class);
	
	@Test
	public void testGetSSLContext() {
		ServerConfig config = new ServerConfig(new Properties());
		config.setParam("https.keyStoreFile", "test.keystore");
		config.setParam("https.keyPassword", "nopassword");
		config.setParam("https.keyStoreType", "JKS");
		config.setParam("https.protocol", "TLS");
		
		SSLSNIContextCreator ctx = new SSLSNIContextCreator(config);
		assertEquals("TLS", ctx.getSSLContext().getProtocol());
	}
	
	@Test
	public void testGetSSLContextSNI() throws Exception {
		ServerConfig config = new ServerConfig(new Properties());
		config.setParam("https.keyStoreFile", "sni-test-keystore.jks");
		config.setParam("https.keyPassword", "nopassword");
		config.setParam("https.keyStoreType", "JKS");
		config.setParam("https.protocol", "TLS");
		config.setParam("https.defaultAlias", "test01.example.com");
		
		SSLSNIContextCreator creator = new SSLSNIContextCreator(config);
		SSLContext ctx = creator.getSSLContext();
		assertEquals("TLS", ctx.getProtocol());
		//LOG.debug(String.join(",", ctx.getServerSocketFactory().getDefaultCipherSuites()));
		LOG.debug(String.join(",", ctx.getServerSocketFactory().getSupportedCipherSuites()));
	}
	
	@Test
	public void testGetSSLContextError() {
		//IllegalArgumentException
		ServerConfig config = new ServerConfig(new Properties());
		String keyStoreFile = "nofile";
		config.setParam("https.keyStoreFile", keyStoreFile);
		config.setParam("https.keyStoreType", "JKS");
		config.setParam("https.defaultAlias", "test01.example.com");

		SSLSNIContextCreator creator = new SSLSNIContextCreator(config);
		try {
			creator.getSSLContext();
			fail();
		} catch (Exception e) {
			//System.out.println(e.getMessage());
			assertEquals("java.lang.IllegalArgumentException: https.keyStoreFile ["+keyStoreFile+"] file not found.", e.getMessage());
		}
	}
	
	@Test
	public void testGetDefaultAlias() {
		ServerConfig config = new ServerConfig(new Properties());
		config.setParam("https.defaultAlias", "www.example.com");
		SSLSNIContextCreator creator = new SSLSNIContextCreator(config);
		assertEquals("www.example.com", creator.getDefaultAlias());

		creator.setDefaultAlias("test.example.com");
		assertEquals("test.example.com", creator.getDefaultAlias());
	}

}
