/*
 * Copyright (c) 2013, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.handler;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ReverseUrl;

public class ReverseHttpRequestFactory {

	public static ReverseHttpRequest getInstance(HttpRequest request, HttpResponse response,
			HttpContext context, ReverseUrl reverseUrl) {
		if (request instanceof HttpEntityEnclosingRequest) {
			return new ReverseHttpEntityEnclosingRequest(request, context, reverseUrl);
		} else {
			return new ReverseHttpRequest(request, context, reverseUrl);
		}
	}
}
