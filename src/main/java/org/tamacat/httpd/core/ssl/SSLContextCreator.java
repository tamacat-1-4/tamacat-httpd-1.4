/*
 * Copyright (c) 2009 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core.ssl;

import javax.net.ssl.SSLContext;

import org.tamacat.httpd.config.ServerConfig;

public interface SSLContextCreator {

	void setServerConfig(ServerConfig serverConfig);
	
	SSLContext getSSLContext();
}
