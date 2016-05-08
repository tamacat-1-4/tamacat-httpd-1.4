/*
 * Copyright (c) 2011, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class DefaultSessionAttributes extends HashMap<String, Object> implements SessionAttributes {

	static final Log LOG = LogFactory.getLog(DefaultSessionAttributes.class);
	
	private static final long serialVersionUID = -8046047687990935361L;

	@Override
	public Object getAttribute(String key) {
		return super.get(key);
	}
	
	@Override
	public Set<String> getAttributeKeys() {
		return super.keySet();
	}

	@Override
	public void removeAttribute(String key) {
		super.remove(key);
	}
	
	@Override
	public void setAttribute(String key, Object value) {
		if (value != null && !(value instanceof Serializable)) {
			LOG.warn("SessionAttributes#setAttribute value is not Serializable: "
				+ value.getClass().getName());
		}
		super.put(key, value);
	}
}
