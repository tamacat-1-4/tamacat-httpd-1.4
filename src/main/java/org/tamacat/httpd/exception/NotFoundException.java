/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.exception;

import org.tamacat.httpd.core.BasicHttpStatus;

/**
 * <p>Throws 404 Not Found.
 */
public class NotFoundException extends HttpException {

	private static final long serialVersionUID = 1L;

	public NotFoundException() {
		super(BasicHttpStatus.SC_NOT_FOUND);
	}

	public NotFoundException(String message) {
		super(BasicHttpStatus.SC_NOT_FOUND, message);
	}

	public NotFoundException(Throwable cause) {
		super(BasicHttpStatus.SC_NOT_FOUND, cause);
	}

	public NotFoundException(String message, Throwable cause) {
		super(BasicHttpStatus.SC_NOT_FOUND, message, cause);
	}
}
