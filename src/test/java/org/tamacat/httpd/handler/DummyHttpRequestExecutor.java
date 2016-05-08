/*
 * Copyright (c) 2014, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.handler;

import java.io.IOException;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;
import org.tamacat.httpd.mock.HttpObjectFactory;

public class DummyHttpRequestExecutor extends HttpRequestExecutor {
	HttpRequest request;
	HttpContext context;

	public DummyHttpRequestExecutor() {
	}

	public DummyHttpRequestExecutor(int waitForContinue) {
		super(waitForContinue);
	}

	@Override
	public HttpResponse execute(
			final HttpRequest request,
			final HttpClientConnection conn,
			final HttpContext context) throws IOException, HttpException {
		this.request = request;
		this.context = context;
		return 	HttpObjectFactory.createHttpResponse(200, "OK");
	}

	public HttpRequest getHttpRequest() {
		return request;
	}

	public HttpContext getHttpContext() {
		return context;
	}
}
