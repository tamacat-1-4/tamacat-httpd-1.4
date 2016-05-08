/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config.lb;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * <p>Thread of HTTP Monitor for back end server.
 *
 * @param <T> target of {@code HealthCheckSupport}.
 */
public class HttpMonitor<T> implements Runnable {

	static final Log LOG = LogFactory.getLog("Monitor");

	private MonitorConfig config;
	private T target;
	private MonitorEvent<T> checkTarget;
	private boolean isNormal = true;

	public void setHealthCheckTarget(
			MonitorEvent<T> checkTarget) {
		this.checkTarget = checkTarget;
	}

	public void setTarget(T target) {
		this.target = target;
	}

	public void setMonitorConfig(MonitorConfig config) {
		this.config = config;
	}

	@Override
	public void run() {
		synchronized(checkTarget) {

			boolean result = check();
			if (isNormal == true && result == false) {
				checkTarget.removeTarget(target);
				isNormal = false;
				LOG.warn("check: " + config.getUrl() + " is down.");
			} else if (isNormal == false && result == true){
				checkTarget.addTarget(target);
				isNormal = true;
				LOG.warn("check: " + config.getUrl() + " is up.");
			}
		}
	}

	protected boolean check() {
		if (config == null) return true;
		HttpClient client = HttpClientBuilder.create().build();
		boolean result = false;
		try {
			long start = System.currentTimeMillis();
			HttpGet request = new HttpGet(config.getUrl());
			HttpResponse response = client.execute(request);
			long time = System.currentTimeMillis() - start;
			LOG.debug(request.getRequestLine() + " " + response.getStatusLine().getStatusCode()+" "+time+"ms");
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = true;
			}
		} catch (Exception e) {
			if (isNormal) LOG.error(e.getMessage());
		}
		return result;
	}

	public boolean isNormal() {
		return isNormal;
	}
}
