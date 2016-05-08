/*
 * Copyright (c) 2011, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;

public interface SessionSerializable {

	void updateSession();
	
	void setSessionStore(SessionStore sessionStore);
}
