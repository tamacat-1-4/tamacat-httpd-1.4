/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core.jmx;

import javax.management.MXBean;

/**
 * <p>Support the reload operation of JMX.
 */
@MXBean
public interface Reloadable {

	/**
	 * <p>Reload the configuration.
	 */
	void reload();
}
