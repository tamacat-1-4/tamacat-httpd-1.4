/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter.acl;

import org.tamacat.util.LimitedCacheLRU;

/**
 * <p>Access URL with cache of LRU algorithm.
 */
public class AccessUrlCache extends LimitedCacheLRU<String, AccessUrl> {

	/**
	 * <p>Construts with limit of reuse the cache.
	 * @param maxSize Limited number of instances.
	 * @param expire Cache expired time(ms).
	 */
	public AccessUrlCache(int maxSize, long expire) {
		super(maxSize, expire);
	}
}
