/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config.lb;

/**
 * <p>It is the setting of the monitor for health check.
 */
public class MonitorConfig {

	private int interval;
	private int timeout;
	private String url;
	
	/**
	 * <p>Get the interval of checking.
	 * @return default 0 (ms).
	 */
	public int getInterval() {
		return interval;
	}
	
	/**
	 * <p>Set the interval of checking.
	 * @param interval
	 */
	public void setInterval(int interval) {
		this.interval = interval;
	}
	
	/**
	 * <p>Get the time-out time for HTTP connection.
	 * @return default 0 (ms)
	 */
	public int getTimeout() {
		return timeout;
	}
	
	/**
	 * <p>Set the time-out time for HTTP connection.
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	/**
	 * <p>Get the monitoring URL.
	 * @return URL
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * <p>Set the monitoring URL.
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
}
