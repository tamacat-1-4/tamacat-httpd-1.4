/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core.jmx;

import java.util.Date;

import javax.management.MXBean;

@MXBean
public interface BasicHttpMonitor {

	int getActiveConnections();
	
	Date getStartedTime();
	
	long getAccessCount();
	
	void resetAccessCount();
	
	long getErrorCount();
	
	void resetErrorCount();
	
}
