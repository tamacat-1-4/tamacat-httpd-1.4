/*
 * Copyright (c) 2010, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.handler;

import java.util.HashMap;

import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerMapper;
import org.apache.http.protocol.UriHttpRequestHandlerMapper;
import org.tamacat.httpd.config.HostServiceConfig;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceConfig;
import org.tamacat.httpd.config.ServiceConfigParser;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * <p>The {@link HttpRequestHandlerMapper} for a virtual host.<br>
 * With this HttpRequestHandlerMapper, I acquire virtual host setting based on
 * a Host request header and return a supporting {@link HttpRequestHandler}.
 */
public class HostRequestHandlerMapper {
	static final Log LOG = LogFactory.getLog(HostRequestHandlerMapper.class);

	/** default key for empty host.*/
	static final String DEFAULT_HOST = "default";

	private HashMap<String, HttpRequestHandlerMapper> hostHandler = new HashMap<>();

	private boolean useVirtualHost = false;

	public HostRequestHandlerMapper create(
			ServerConfig serverConfig, String componentsXML) {
		HttpHandlerFactory factory = new DefaultHttpHandlerFactory(
				componentsXML, getClass().getClassLoader());

		HostServiceConfig hostConfig = new ServiceConfigParser(serverConfig).getConfig();
		for (String host : hostConfig.getHosts()) {
			UriHttpRequestHandlerMapper mapper = new UriHttpRequestHandlerMapper();
			ServiceConfig serviceConfig = hostConfig.getServiceConfig(host);
			for (ServiceUrl serviceUrl : serviceConfig.getServiceUrlList()) {
				HttpHandler handler = factory.getHttpHandler(serviceUrl);
				if (handler != null) {
					LOG.info(serviceUrl.getServerConfig().getPort() + ":" + serviceUrl.getPath() + " - " + serviceUrl.getHandlerName()
						+ " (class="+handler.getClass().getName() + ")");
					mapper.register(serviceUrl.getPath() + "*", handler);
				} else {
					LOG.warn(serviceUrl.getPath() + " HttpHandler is not found.");
				}
			}
			this.setHostRequestHandlerMapper(host, mapper);
		}
		return this;
	}

	/**
	 * <p>Set the Host and {@link HttpRequestHandlerMapper}.
	 * @param host parameter is null then set the default {@link HttpRequestHandlerMapper}.
	 * @param mapper
	 */
	public void setHostRequestHandlerMapper(String host, HttpRequestHandlerMapper mapper) {
		if (host == null) {
			host = DEFAULT_HOST;
		}
		if (useVirtualHost == false && hostHandler.size() >= 1) {
			useVirtualHost = true;
		}
		if (host.equals(DEFAULT_HOST) == false) {
			LOG.info("add virtual host: " + host + "=" + mapper.getClass().getName());
		}
		hostHandler.put(host.replaceAll("http://", "").replaceAll("https://", ""), mapper);
	}

	/**
	 * <p>Lookup the HttpRequestHandler for Host request header.
	 * @param request
	 * @param context
	 * @return HttpRequestHandler
	 */
	public HttpRequestHandler lookup(HttpRequest request, HttpContext context) {
		HttpRequestHandlerMapper mapper = null;
		if (useVirtualHost) {
			String host = RequestUtils.getRequestHost(request, context);
			if (host == null) {
				host = DEFAULT_HOST;
			}
			mapper = hostHandler.get(host);
		}
		if (mapper == null) {
			mapper = hostHandler.get(DEFAULT_HOST);
		}
		if (LOG.isTraceEnabled() && mapper != null) {
			LOG.trace("handler: " + mapper.getClass().getName());
		}
		HttpRequestHandler handler = null;
		if (mapper != null) {
			handler = mapper.lookup(request);
		}
		return handler;
	}
}
