/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.exception;

import org.tamacat.httpd.core.BasicHttpStatus;

/**
 * <p>Throws 401 Unauthorized
 */
public class UnauthorizedException extends HttpException {

	private static final long serialVersionUID = 1L;
	
	public UnauthorizedException() {
		super(BasicHttpStatus.SC_UNAUTHORIZED);
	}
	
	public UnauthorizedException(String message) {
		super(BasicHttpStatus.SC_UNAUTHORIZED, message);
	}
}
