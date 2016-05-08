/*
 * Copyright (c) 2014 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core.ssl;

import java.net.URL;
import java.security.KeyStore;
import java.util.Enumeration;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509ExtendedKeyManager;

import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.io.RuntimeIOException;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.ClassUtils;
import org.tamacat.util.StringUtils;

/**
 * SSLContext for SNI (Multiple domain support)
 * "Server Name Indication" of TLS Extensions (RFC 6066).
 *
 * Add default domain in server.properties.
 * ex) https.defaultAlias=www.examples.com
 */
public class SSLSNIContextCreator extends DefaultSSLContextCreator {

	static final Log LOG = LogFactory.getLog(SSLSNIContextCreator.class);

	protected static final String DEFAULT_ALIAS_KEY = "https.defaultAlias";

	protected String defaultAlias;

	public SSLSNIContextCreator() {}

	public SSLSNIContextCreator(ServerConfig serverConfig) {
		super(serverConfig);
	}

	@Override
	public void setServerConfig(ServerConfig serverConfig) {
		super.setServerConfig(serverConfig);
		setDefaultAlias(serverConfig.getParam(DEFAULT_ALIAS_KEY));
	}

	public SSLContext getSSLContext() {
		String defaultAlias = getDefaultAlias();
		if (StringUtils.isEmpty(defaultAlias)) {
			return super.getSSLContext();
		}
		try {
			URL url = ClassUtils.getURL(keyStoreFile);
			if (url == null) {
				throw new IllegalArgumentException("https.keyStoreFile ["+keyStoreFile+"] file not found.");
			}
			KeyStore keystore = KeyStore.getInstance(type.name());
			keystore.load(url.openStream(), keyPassword);
			KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmfactory.init(keystore, keyPassword);

			X509ExtendedKeyManager x509KeyManager = null;
			KeyManager[] keymanagers = kmfactory.getKeyManagers();
			for (KeyManager keyManager : keymanagers) {
				if (keyManager instanceof X509ExtendedKeyManager) {
					x509KeyManager = (X509ExtendedKeyManager) keyManager;
					break;
				}
			}
			SSLContext sslcontext = SSLContext.getInstance(protocol.getName());
			if (x509KeyManager == null) {
				sslcontext.init(keymanagers, null, null);
			} else {
				SNIKeyManager sniKeyManager = new SNIKeyManager(x509KeyManager, defaultAlias);
				sslcontext.init(new KeyManager[] { sniKeyManager }, null, null);
				if (LOG.isDebugEnabled()) {
					LOG.debug("TLS/SNI default=" + defaultAlias);
					Enumeration<String> en = keystore.aliases();
					while (en.hasMoreElements()) {
						LOG.debug("TLS/SNI alias=" + en.nextElement());
					}
				}
			}
			return sslcontext;
		} catch (Exception e) {
			throw new RuntimeIOException(e);
		}
	}

	public void setDefaultAlias(String defaultAlias) {
		if (StringUtils.isNotEmpty(defaultAlias)) {
			this.defaultAlias = defaultAlias;
		}
	}

	public String getDefaultAlias() {
		return defaultAlias;
	}
}
