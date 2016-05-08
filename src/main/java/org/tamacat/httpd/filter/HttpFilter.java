/*
 * Copyright (c) 2010, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import org.tamacat.httpd.config.ServiceUrl;

/**
 * <p>{@code HttpFilter} is an interface used as the base for filtering a request and a response.
 * 
 * <p>You should not make HttpFilter singleton. 
 * Please be sure to attach singleton="false" in components.xml. 
 */
public interface HttpFilter {
	
	/**
	 * This key is used in order to hold the exception which occurred in HttpFilter.
	 */
	String EXCEPTION_KEY = "org.tamacat.httpd.filter.HttpFilter.Exception";
	
	/**
	 * This key is skip handler process.
	 */
	String SKIP_HANDLER_KEY = "org.tamacat.httpd.filter.HttpFilter.SkipHandler";
	
	/**
	 * It is an initialization method performed only once at the time of starting.
	 * @param serviceUrl
	 */
	void init(ServiceUrl serviceUrl);
}
