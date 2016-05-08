/*
 * Copyright (c) 2014, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core.jmx;

import javax.management.MXBean;

@MXBean
public interface ReloadaleMXServer {
	void registerMXServer();

	void unregisterMXServer();
}
