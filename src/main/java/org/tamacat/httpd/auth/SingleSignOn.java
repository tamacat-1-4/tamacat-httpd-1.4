/*
 * Copyright (c) 2011, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

/**
 * Interface of Single Sign-On.
 */
public interface SingleSignOn {

	/**
	 * It performs, when user authentication is execute sign.
	 * 
	 * @param remoteUser
	 * @param request
	 * @param response
	 * @param context
	 */
	void sign(String remoteUser, HttpRequest request, HttpResponse response, HttpContext context);

	/**
	 * Already signed returns true.
	 * 
	 * @param request
	 * @param context
	 * @return
	 */
	boolean isSigned(HttpRequest request, HttpResponse response, HttpContext context);

	/**
	 * Get a signed user, when isSigned() returns true. if user is unknown or
	 * object is null, then throws UnauthorizedException.
	 * 
	 * @param request
	 * @param context
	 * @return signed username. (not returns null)
	 */
	String getSignedUser(HttpRequest request, HttpResponse response, HttpContext context);

	/**
	 * unsign
	 * 
	 * @param remoteUser
	 * @param request
	 * @param response
	 * @param context
	 * @since 1.1
	 */
	void unsign(String remoteUser, HttpRequest request, HttpResponse response, HttpContext context);
}
