/*
 * Copyright (c) 2011, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.session;

import java.io.Serializable;
import java.util.Set;

public interface SessionAttributes extends Serializable {

	Object getAttribute(String key);

	Set<String> getAttributeKeys();

	void removeAttribute(String key);

	void setAttribute(String key, Object value);

	void clear();
}