/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

/**
 * <p>{@code RequestFilter} is execute from
 * {@link HttpRequestHandler#handle} method.
 */
public interface RequestFilter extends HttpFilter {
	
	String SKIP_REQUEST_FILTER_KEY = "org.tamacat.httpd.filter.RequestFilter.SkipFilter";
	
	/**
	 * This method is performed before a request. 
	 * @param request
	 * @param response
	 * @param context
	 */
	void doFilter(HttpRequest request, HttpResponse response, 
		HttpContext context);
}
