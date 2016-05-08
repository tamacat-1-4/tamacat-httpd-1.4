/*
 * Copyright (c) 2010, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core.jmx;

import javax.management.MXBean;

@MXBean
public interface PerformanceCounterMonitor extends BasicHttpMonitor, PerformanceCounter {

	String getPath();
}
