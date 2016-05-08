/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config.lb;

/**
 * <p>It is interface to express that I support a health check function.
 * 
 * @param <T> target of health check object.
 */
public interface HealthCheckSupport<T> extends MonitorEvent<T> {

	/**
	 * <p>Start the {@link HttpMonitor} thread.
	 */
	void startHealthCheck();
}
