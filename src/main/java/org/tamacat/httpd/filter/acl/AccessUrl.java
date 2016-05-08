/*
 * Copyright (c) 2008, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter.acl;

import org.tamacat.util.LimitedCacheObject;

/**
 * <p>This interface is represents a accessible URL.
 */
public interface AccessUrl extends LimitedCacheObject {

	/**
	 * <p>When the accessible URL returns true.
	 * @return true: accessible.
	 */
	boolean isSuccess();
}
