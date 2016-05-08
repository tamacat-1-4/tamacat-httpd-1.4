/*
 * Copyright (c) 2010, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core.jmx;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class URLBasicCounter {

	static final Log LOG = LogFactory.getLog(URLBasicCounter.class);

	private Map<String, ObjectName> onames = new LinkedHashMap<>();

	private static final Map<String, BasicCounter> counters = new HashMap<>();
	private String objectName = "org.tamacat.httpd:type=URL#";

	public BasicCounter getCounter(String url) {
		return counters.get(url);
	}

	/**
	 * <p>Set the base ObjectName for JMX.
	 * ObjectName is append the URL path.<br>
	 * default: "org.tamacat.httpd:type=URL/${path}"
	 * @param objectName
	 */
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public BasicCounter register(String url) {
		BasicCounter counter = new BasicCounter();
		counter.setPath(url);
		try {
			ObjectName oname = new ObjectName(objectName + url);
			onames.put(url, oname);
			counters.put(url, counter);

			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			server.registerMBean(counter, oname);
		} catch (InstanceAlreadyExistsException e) {
		} catch (Exception e) {
			LOG.error(e);
		}
		return counter;
	}
}
