/*
 * Copyright (c) 2011, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import org.apache.http.protocol.HttpContext;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * <p>
 * {@code AbstractAuthComponent} is common implementation for
 * {@code AuthComponent}. AuthUser can be acquired from AuthUserManager when
 * attested.
 * 
 * @param <T> extends AuthUser
 */
public abstract class AbstractAuthComponent<T extends AuthUser> implements AuthComponent<T> {

	static final Log LOG = LogFactory.getLog(AbstractAuthComponent.class);

	protected AuthUserCache cache;
	protected int maxCacheSize = 100;
	protected long cacheExpire = 30000;

	public void setMaxCacheSize(int maxCacheSize) {
		this.maxCacheSize = maxCacheSize;
	}

	public void setCacheExpire(long cacheExpire) {
		this.cacheExpire = cacheExpire;
	}

	@Override
	public void init() {
		if (maxCacheSize > 0 && cacheExpire > 0) {
			cache = new AuthUserCache(maxCacheSize, cacheExpire);
		}
	}

	@Override
	public void release() {
	}

	@Override
	public boolean check(String id, String pass, HttpContext context) {
		if (id != null && pass != null) {
			if (cache != null) {
				CacheSupportAuthUser u = cache.get(id);
				if (u != null) {
					if (id.equals(u.getAuthUsername()) && pass.equals(u.getAuthPassword())) {
						LOG.debug("use cache: " + u);
						return true;
					}
				}
			}
			T user = getAuthUser(id, context);
			if (user != null) {
				if (id.equals(user.getAuthUsername()) && pass.equals(user.getAuthPassword())) {
					if (cache != null && user instanceof CacheSupportAuthUser) {
						cache.put(id, (CacheSupportAuthUser) user);
					}
					return true;
				}
			}
		}
		return false;
	}
}
