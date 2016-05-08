/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.exception;

import org.tamacat.httpd.core.BasicHttpStatus;

/**
 * <p>Throws 503 Service Unavailable.
 */
public class ServiceUnavailableException extends HttpException {

	private static final long serialVersionUID = 1L;

	public ServiceUnavailableException() {
		super(BasicHttpStatus.SC_SERVICE_UNAVAILABLE);
	}

	public ServiceUnavailableException(Throwable cause) {
		super(BasicHttpStatus.SC_SERVICE_UNAVAILABLE, cause);
	}

	public ServiceUnavailableException(String message) {
		super(BasicHttpStatus.SC_SERVICE_UNAVAILABLE, message);
	}

	public ServiceUnavailableException(String message,
			Throwable cause) {
		super(BasicHttpStatus.SC_SERVICE_UNAVAILABLE, message, cause);
	}
}
