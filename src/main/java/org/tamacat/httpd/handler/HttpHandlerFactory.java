/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.handler;

import org.tamacat.httpd.config.ServiceUrl;

/**
 * <p>The factory interface of HttpHandler.
 */
public interface HttpHandlerFactory {

	/**
	 * <p>Returns the {@code HttpHandler}.
	 * @param serviceUrl
	 * @return HttpHandler
	 */
	HttpHandler getHttpHandler(ServiceUrl serviceUrl);
}
