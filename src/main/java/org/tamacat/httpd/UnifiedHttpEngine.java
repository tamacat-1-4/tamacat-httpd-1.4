/*
 * Copyright (c) 2019 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd;

import java.util.ArrayList;
import java.util.List;

import org.tamacat.httpd.core.HttpEngine;
import org.tamacat.httpd.middleware.Middleware;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * Unified HttpEngine implementation for running in one Java process.
 * Startup tamacat-httpd with middlewares.
 */
public class UnifiedHttpEngine extends HttpEngine {

	static final Log LOG = LogFactory.getLog(UnifiedHttpEngine.class);
	
	List<Middleware> middlewares = new ArrayList<>();
	
	/**
	 * Add the middlewares.
	 * @param middleware
	 */
	public void setMiddleware(Middleware middleware) {
		middlewares.add(middleware);
	}

	/**
	 * Startup initialize httpd and middlewares.
	 */
	@Override
	public void init() {
		super.init();
		
		try {
			for (Middleware middleware : middlewares) {
				middleware.startup();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			LOG.info("start httpd");
		}
	}
	
	@Override
	public void stopHttpd() {
		try {
			for (Middleware middleware : middlewares) {
				middleware.shutdown();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			super.stopHttpd();
		}
	}
}