/*
 * Copyright (c) 2013, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class HeaderLoggingFilter implements RequestFilter, ResponseFilter {

	static final Log LOG = LogFactory.getLog("org.tamacat.httpd.debug.Header");

	@Override
	public void init(ServiceUrl serviceUrl) {}

	@Override
	public void doFilter(HttpRequest request, HttpResponse response,
			HttpContext context) {
		LOG.info("[request] " + request.getRequestLine());
		if (LOG.isDebugEnabled()) {
			for (Header h : request.getAllHeaders()) {
				LOG.debug("[request] " + h);
			}
		}
	}

	@Override
	public void afterResponse(HttpRequest request, HttpResponse response,
			HttpContext context) {
		LOG.info("[response] " + response.getStatusLine());
		if (LOG.isDebugEnabled()) {
			for (Header h : response.getAllHeaders()) {
				LOG.debug("[response] " + h);
			}
		}
	}
}
