/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.examples;

import org.tamacat.httpd.filter.AbstractAccessControlFilter;
import org.tamacat.httpd.filter.acl.AccessUrl;

public class AccessControlFilter extends AbstractAccessControlFilter {

	@Override
	protected boolean isSuccess(String username, String url) {
		AccessUrl accessUrl = getCachedAccessUrl(username, url);
		if (accessUrl != null && accessUrl.isSuccess()) {
			return true;
		}
		//
		// DB access code
		//
		accessUrl = new AccessUrlImpl(true);
		setAccessUrlCache(username, url, accessUrl);
		return accessUrl.isSuccess();
	}
}
