/*
 * Copyright (c) 2012, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import java.util.HashSet;
import java.util.LinkedHashMap;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;

/**
 * The response filter of append or remove response header.
 */
public class CustomResponseHeaderFilter implements ResponseFilter {
	
	ServiceUrl serviceUrl;
	LinkedHashMap<String, String> appendHeaders = new LinkedHashMap<>();
	HashSet<String> removeHeaders = new HashSet<>();
	
	@Override
	public void init(ServiceUrl serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	@Override
	public void afterResponse(HttpRequest request, HttpResponse response,
			HttpContext context) {
		for (String name : appendHeaders.keySet()) {
			response.addHeader(name, appendHeaders.get(name));
		}
		for (String name : removeHeaders.toArray(new String[removeHeaders.size()])) {
			response.removeHeaders(name);
		}
	}
	
	/**
	 * Append the response header.
	 * @param name
	 * @param value
	 */
	public void setAppendHeader(String name, String value) {
		appendHeaders.put(name, value);
	}
	
	/**
	 * Remove the response header.
	 * @param name
	 */
	public void setRemoveHeader(String name) {
		removeHeaders.add(name);
	}
}
