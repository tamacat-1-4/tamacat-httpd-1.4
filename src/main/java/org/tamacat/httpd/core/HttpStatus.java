/*
 * Copyright (c) 2010, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

/**
 * <p>Constants of HTTP Status Code and Reason Phrase.<br>
 * RFC 2616 Hypertext Transfer Protocol -- HTTP/1.1 (June 1999)
 * @see {@link http://www.ietf.org/rfc/rfc2616.txt}
 */
public interface HttpStatus {
	
	int getStatusCode();
	
	String getReasonPhrase();
	
	boolean isInformational();
	
	boolean isSuccess();
	
	boolean isRedirection();
	
	boolean isClientError();
	
	boolean isServerError();
}
