/*
 * Copyright (c) 2011, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;

public interface SessionStore extends SessionMonitor {
	
	void store(Session session);
	
	Session load(String id);
	
	void delete(String id);
	
	void release();
}
