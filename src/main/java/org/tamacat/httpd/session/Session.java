/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * <p>{@code Session} interface like HttpSession of Servlet-API.
 */
public interface Session extends Serializable {

	void setId(String id);
	
	String getId();
	
	Object getAttribute(String key);
	
	void setAttribute(String key, Object value);
	
	void removeAttribute(String key);
	
	Set<String> getAttributeKeys();
	
	void setSessionAttributes(SessionAttributes attributes);
	
	SessionAttributes getSessionAttributes();
	
	Date getCreationDate();
	
	Date getLastAccessDate();
	
	void setLastAccessDate(Date lastAccessDate);
	
	int getMaxInactiveInterval();
	
	void setMaxInactiveInterval(int maxInactiveInterval);
	
	void invalidate();
	
	boolean isInvalidate();
}
