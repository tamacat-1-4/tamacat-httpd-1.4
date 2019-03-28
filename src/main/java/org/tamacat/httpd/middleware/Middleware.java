/*
 * Copyright (c) 2019 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.middleware;

/**
 * The Middleware is Startup Hook interface for Unified HttpEngine.
 */
public interface Middleware {
	
	/**
	 * Startup Hook after HttpEngine startup.
	 */
	void startup();
	
	/**
	 * Shutdown Hook before HttpEngine shutdown.
	 */
	void shutdown();
}
