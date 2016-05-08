/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.handler;

import org.tamacat.di.DI;
import org.tamacat.di.DIContainer;
import org.tamacat.httpd.config.ServiceUrl;

/**
 * <p>The default implements of {@link HttpHandlerFactory}.
 * Using the {@link DIContainer}, configuration file is {@code components.xml}.
 */
public class DefaultHttpHandlerFactory implements HttpHandlerFactory {

	private DIContainer di;
	
	public DefaultHttpHandlerFactory(String xml) {
		di = DI.configure(xml, getClass().getClassLoader());
	}
	
	public DefaultHttpHandlerFactory(String xml, ClassLoader loader) {
		di = DI.configure(xml, loader);
	}
	
	@Override
	public HttpHandler getHttpHandler(ServiceUrl serviceUrl) {
		HttpHandler httpHandler = di.getBean(serviceUrl.getHandlerName(), HttpHandler.class);
		httpHandler.setServiceUrl(serviceUrl);
		return httpHandler;
	}
}
