/*
 * Copyright (c) 2011, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;

public interface SessionFactory extends SessionMonitor {

	Session getSession(String id);
	Session getSession(String id, boolean isCreate);
	
	Session createSession();
	
	/**
	 * check the session object.
	 * if session invalidate then return null. 
	 * @param id
	 * @return
	 */
	Session checkSession(String id);
	
	void invalidate(Session session);
	
	void release();
	
	void setSessionStore(SessionStore sessionStore);
}
