/*
 * Copyright (c) 2015 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import java.io.File;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * <p>
 * Replace the "${server.home}" variable to server home directory.
 * <pre>
 * -Dserver.home=/usr/local/tamacat-httpd
 * or
 * -Duser.dir=~/tamacat-httpd
 * </pre>
 */
public class ServerUtils {
	
	static final Log LOG = LogFactory.getLog(ServerUtils.class);
	
	protected static String serverHome;

	static {
		try {
			serverHome = System.getProperty("server.home");
			if (serverHome == null) serverHome = System.getProperty("user.dir");
			File home = new File(serverHome);
			serverHome = home.getCanonicalPath();
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	/**
	 * Get ${server.home}
	 * @param docsRoot
	 * @since 1.4-20180904
	 */
	public static String getServerHome() {
		return serverHome;
	}

	public static String getServerDocsRoot(String docsRoot) {
		return docsRoot.replace("${server.home}", serverHome).replace("\\", "/");
	}
}
