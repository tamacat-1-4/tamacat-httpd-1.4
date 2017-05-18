/*
 * Copyright (c) 2009-2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core.ssl;

import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.io.RuntimeIOException;
import org.tamacat.util.ClassUtils;

/**
 * <p>
 * The {@link SSLContext} create from {@link ServerConfig} or setter methods.
 */
public class DefaultSSLContextCreator implements SSLContextCreator {

	protected String keyStoreFile;
	protected char[] keyPassword;
	protected KeyStoreType type = KeyStoreType.JKS;
	protected SSLProtocol protocol = SSLProtocol.TLSv1_2;

	public void setKeyStoreFile(String keyStoreFile) {
		this.keyStoreFile = keyStoreFile;
	}

	public void setKeyPassword(String keyPassword) {
		this.keyPassword = keyPassword.toCharArray();
	}

	/**
	 * <p>
	 * Default constructor.
	 */
	public DefaultSSLContextCreator() {
	}

	/**
	 * <p>
	 * The constructor of setting values from {@code ServerConfig}.
	 */
	public DefaultSSLContextCreator(ServerConfig serverConfig) {
		setServerConfig(serverConfig);
	}

	/**
	 * @sinse 1.2
	 * @param serverConfig
	 */
	public void setServerConfig(ServerConfig serverConfig) {
		setKeyStoreFile(serverConfig.getParam("https.keyStoreFile", ""));
		setKeyPassword(serverConfig.getParam("https.keyPassword", ""));
		setKeyStoreType(serverConfig.getParam("https.keyStoreType", "JKS"));
		setSSLProtocol(serverConfig.getParam("https.protocol", "TLSv1_2"));
	}

	public void setKeyStoreType(String type) {
		this.type = KeyStoreType.valueOf(type);
	}

	public void setKeyStoreType(KeyStoreType type) {
		this.type = type;
	}

	public void setSSLProtocol(String protocol) {
		this.protocol = SSLProtocol.valueOf(protocol);
	}

	public void setSSLProtocol(SSLProtocol protocol) {
		this.protocol = protocol;
	}

	public SSLContext getSSLContext() {
		try {
			URL url = ClassUtils.getURL(keyStoreFile);
			if (url == null) {
				throw new IllegalArgumentException("https.keyStoreFile ["+keyStoreFile+"] file not found.");
			}
			KeyStore keystore = KeyStore.getInstance(type.name());
			keystore.load(url.openStream(), keyPassword);

			KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmfactory.init(keystore, keyPassword);
			KeyManager[] keymanagers = kmfactory.getKeyManagers();
			SSLContext sslcontext = SSLContext.getInstance(protocol.getName());
			sslcontext.init(keymanagers, null, null);
			return sslcontext;
		} catch (Exception e) {
			throw new RuntimeIOException(e);
		}
	}
}
