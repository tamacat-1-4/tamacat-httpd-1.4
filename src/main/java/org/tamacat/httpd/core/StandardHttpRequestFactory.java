/*
 * Copyright (c) 2011, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import org.apache.http.HttpRequest;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.RequestLine;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.tamacat.httpd.util.RequestUtils;

public class StandardHttpRequestFactory extends DefaultHttpRequestFactory {

	static final String[] RFC2616_COMMON_METHODS = {
		"GET"
	};

	static final String[] RFC2616_ENTITY_ENC_METHODS = {
		"POST",
		"PUT"
	};

	static final String[] RFC2616_SPECIAL_METHODS = {
		"HEAD",
		"OPTIONS",
		"DELETE",
		"TRACE",
		"CONNECT"
	};

	public StandardHttpRequestFactory() {}

	static boolean isOneOf(final String[] methods, final String method) {
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].equalsIgnoreCase(method)) {
				return true;
			}
		}
		return false;
	}

	public HttpRequest newHttpRequest(RequestLine requestline)
			throws MethodNotSupportedException {
		if (requestline == null) {
			throw new IllegalArgumentException("Request line may not be null");
		}
		String method = requestline.getMethod();
		requestline = RequestUtils.getRequestLine(requestline);
		if (isOneOf(RFC2616_COMMON_METHODS, method)) {
			return new BasicHttpRequest(requestline);
		} else if (isOneOf(RFC2616_ENTITY_ENC_METHODS, method)) {
			return new BasicHttpEntityEnclosingRequest(requestline);
		} else if (isOneOf(RFC2616_SPECIAL_METHODS, method)) {
			return new BasicHttpRequest(requestline);
		} else {
			throw new MethodNotSupportedException(method +  " method not supported");
			//return new BasicHttpEntityEnclosingRequest(requestline);
		}
	}

	public HttpRequest newHttpRequest(final String method, String uri)
			throws MethodNotSupportedException {
		uri = RequestUtils.getRequestPath(uri);
		if (isOneOf(RFC2616_COMMON_METHODS, method)) {
			return new BasicHttpRequest(method, uri);
		} else if (isOneOf(RFC2616_ENTITY_ENC_METHODS, method)) {
			return new BasicHttpEntityEnclosingRequest(method, uri);
		} else if (isOneOf(RFC2616_SPECIAL_METHODS, method)) {
			return new BasicHttpRequest(method, uri);
		} else {
			//return new BasicHttpEntityEnclosingRequest(method, uri);
			throw new MethodNotSupportedException(method +  " method not supported");
		}
	}
}
