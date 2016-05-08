/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config;

import java.net.InetSocketAddress;
import java.net.URL;

import org.apache.http.HttpHost;

/**
 * <p>The ReverseUrl interface is a setup of Reverse Proxy URL.
 */
public interface ReverseUrl {

	/**
	 * <p>Returns the {@link ServiceURL}.
	 * @return {@code ServiceUrl}
	 */
	ServiceUrl getServiceUrl();

	/**
	 * <p>Returns the host URL.
	 * @return URL
	 */
	URL getHost();

	/**
	 * <p>Set the host URL.
	 * @param host
	 */
	void setHost(URL host);

	/**
	 * <p>Returns the URL of backend server.
	 * @return URL
	 */
	URL getReverse();

	/**
	 * <p>Set the reverse URL for backend server.
	 * @param url
	 */
	void setReverse(URL url);

	/**
	 * <p>Returns the {@code ReverseUrl} with path.
	 * @param path
	 * @return URL
	 */
	URL getReverseUrl(String path);

	/**
	 * <p>Returns the convert requested URL with path.
	 * It uses at the time of Location header conversion.
	 * @param path
	 * @return convertend URL string.
	 */
	String getConvertRequestedUrl(String path);

	/**
	 * <p>Returns the backend server's {@link InetSocketAddress}.
	 * @return {@code InetSocketAddress}
	 */
	InetSocketAddress getTargetAddress();

	/**
	 * <p>Returns the backend server's {@link HttpHost}.
	 * @return {@code HttpHost}
	 */
	HttpHost getTargetHost();

}
