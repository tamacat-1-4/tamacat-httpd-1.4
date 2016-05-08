/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core.jmx;

import javax.management.MXBean;

/**
 * <p>Support the monitor, reload and restart operation of JMX for Httpd.
 */
@MXBean
public interface JMXReloadableHttpd	extends Reloadable {

	/**
	 * <p>start the Httpd.
	 */
	void startHttpd();

	/**
	 * <p>stop the Httpd.
	 */
	void stopHttpd();

	void restartHttpd();

	//int getMaxServerThreads();

	//void setMaxServerThreads(int max);

	//void registerMXServer();

	//void unregisterMXServer();
}
