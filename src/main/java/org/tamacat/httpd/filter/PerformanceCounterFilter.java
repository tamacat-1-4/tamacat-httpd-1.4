/*
 * Copyright (c) 2010, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import java.net.URL;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.core.jmx.BasicCounter;
import org.tamacat.httpd.core.jmx.URLBasicCounter;

public class PerformanceCounterFilter implements RequestFilter, ResponseFilter {

	private static final URLBasicCounter urlCounter = new URLBasicCounter();

	protected ServiceUrl serviceUrl;

	/**
	 * <p>Set the base ObjectName for JMX.
	 * ObjectName is append the URL path.<br>
	 * default: "org.tamacat.httpd:type=URL/${path}"
	 * @param objectName
	 */
	public void setObjectName(String objectName) {
		urlCounter.setObjectName(objectName);
	}

	@Override
	public void doFilter(HttpRequest request, HttpResponse response,
			HttpContext context) {
		BasicCounter counter = urlCounter.getCounter(getPath(serviceUrl));
		if (counter != null) counter.countUp();
	}

	@Override
	public void init(ServiceUrl serviceUrl) {
		this.serviceUrl = serviceUrl;
		urlCounter.register(getPath(serviceUrl));
	}

	@Override
	public void afterResponse(HttpRequest request, HttpResponse response,
			HttpContext context) {
		BasicCounter counter = urlCounter.getCounter(getPath(serviceUrl));
		if (counter != null) counter.countDown();
	}

	/**
	 *
	 * @param serviceUrl
	 * @return
	 */
	protected String getPath(ServiceUrl serviceUrl) {
		URL host = serviceUrl.getHost();
		String name = host != null?
			host.getAuthority().replace(":", "_") + serviceUrl.getPath() : serviceUrl.getPath();
		return name;
	}
}
