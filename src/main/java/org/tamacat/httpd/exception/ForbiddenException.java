/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.exception;

import org.tamacat.httpd.core.BasicHttpStatus;

/**
 * <p>Throws 403 Forbidden.
 */
public class ForbiddenException extends HttpException {

	private static final long serialVersionUID = 1L;

	public ForbiddenException(){
		super(BasicHttpStatus.SC_FORBIDDEN);
	}
	
	public ForbiddenException(String message) {
		super(BasicHttpStatus.SC_FORBIDDEN, message);
	}
	
	public ForbiddenException(Throwable cause) {
		super(BasicHttpStatus.SC_FORBIDDEN, cause);
	}
}
