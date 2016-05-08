/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.exception;

import org.tamacat.httpd.core.HttpStatus;

/**
 * <p>The {@code HttpException} exception represents a HTTP fault.
 */
public class HttpException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final HttpStatus httpStatus;
	
	public HttpException(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
	
	public HttpException(HttpStatus httpStatus, Throwable cause) {
		super(cause);
		this.httpStatus = httpStatus;
	}
	
	public HttpException(HttpStatus httpStatus, String message) {
		super(message);
		this.httpStatus = httpStatus;
	}
	
	public HttpException(HttpStatus httpStatus, String message, Throwable cause) {
		super(message, cause);
		this.httpStatus = httpStatus;
	}
	
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}
