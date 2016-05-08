/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;

/**
 * <p>The interface of session binding.
 */
public interface SessionListener {

	void sessionCreated(Session session);
	
	void sessionDestroyed(Session session);
}
