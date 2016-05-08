/*
 * Copyright (c) 2010, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

/**
 * <p>{@code ResponseFilter} performed at the end of a {@link HttpRequestHandler#handle} method.
 */
public interface ResponseFilter extends HttpFilter {
	
	String SKIP_RESPONSE_FILTER_KEY = "org.tamacat.httpd.filter.ResponseFilter.SkipFilter";
	
	/**
	 * This method is performed after a response. 
	 * @param request
	 * @param response
	 * @param context
	 */
	void afterResponse(HttpRequest request, HttpResponse response, 
		HttpContext context);
}
