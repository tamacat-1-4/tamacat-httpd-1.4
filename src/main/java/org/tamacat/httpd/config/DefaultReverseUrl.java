/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpHost;
import org.tamacat.httpd.core.jmx.BasicCounter;
import org.tamacat.httpd.core.jmx.PerformanceCounter;
import org.tamacat.util.CloneUtils;

/**
 * <p>
 * The default implements of {@link ReverseUrl}.
 */
public class DefaultReverseUrl implements ReverseUrl, PerformanceCounter, Cloneable {

	private ServiceUrl serviceUrl;
	private URL reverseUrl;
	private InetSocketAddress targetAddress;
	private PerformanceCounter counter;

	/**
	 * <p>
	 * Constructs with the specified {@link ServiceUrl}.
	 *
	 * @param serviceUrl
	 */
	public DefaultReverseUrl(ServiceUrl serviceUrl) {
		this.serviceUrl = serviceUrl;
		counter = new BasicCounter(serviceUrl.getServerConfig());
	}

	@Override
	public ServiceUrl getServiceUrl() {
		return serviceUrl;
	}

	@Override
	public URL getHost() {
		return serviceUrl.getHost();
	}

	@Override
	public void setHost(URL host) {
		try {
			serviceUrl.setHost(new URL(host.getProtocol(), host.getHost(), host.getPort(), ""));
		} catch (Exception e) {
			// none
		}
	}

	@Override
	public URL getReverse() {
		return reverseUrl;
	}

	@Override
	public URL getReverseUrl(String path) {
		String p = serviceUrl.getPath();
		if (path != null && p != null && path.startsWith(p)) {
			String distUrl = path.replaceFirst(serviceUrl.getPath(), reverseUrl.getPath());
			try {
				int port = reverseUrl.getPort();
				if (port == -1) {
					port = reverseUrl.getDefaultPort();
				}
				return new URL(reverseUrl.getProtocol(), reverseUrl.getHost(), port, distUrl);
			} catch (MalformedURLException e) {
			}
		}
		return null;
	}

	@Override
	public InetSocketAddress getTargetAddress() {
		return targetAddress;
	}

	@Override
	public HttpHost getTargetHost() {
		return new HttpHost(targetAddress.getHostName(), targetAddress.getPort(), reverseUrl.getProtocol());
	}


	@Override
	public void setReverse(URL reverseUrl) {
		this.reverseUrl = reverseUrl;
		int port = reverseUrl.getPort();
		if (port == -1)
			port = reverseUrl.getDefaultPort();
		targetAddress = new InetSocketAddress(reverseUrl.getHost(), port);
	}

	@Override
	/**
	 * path: http://localhost:8080/examples/servlet
	 *   =>  http://localhost/examples2/servlet
	 */
	public String getConvertRequestedUrl(String path) {
		URL host = getHost(); // requested URL (path is deleted)
		if (path != null && host != null) {
			return path.replaceFirst(
				reverseUrl.getProtocol() + "://" + reverseUrl.getAuthority(), host.toString())
					.replace(reverseUrl.getPath(), getServiceUrl().getPath());
		} else {
			return path;
		}
	}

	@Override
	public DefaultReverseUrl clone() throws CloneNotSupportedException {
		DefaultReverseUrl clone = (DefaultReverseUrl) super.clone();
		if (this.serviceUrl != null) {
			clone.serviceUrl = CloneUtils.clone(this.serviceUrl);
		}
		return clone;
	}

	@Override
	public int getActiveConnections() {
		return counter.getActiveConnections();
	}

	@Override
	public int countUp() {
		return counter.countUp();
	}

	@Override
	public int countDown() {
		return counter.countDown();
	}

	@Override
	public void reset() {
		counter.reset();
	}

	@Override
	public long getAverageResponseTime() {
		return counter.getAverageResponseTime();
	}

	@Override
	public long getMaximumResponseTime() {
		return counter.getMaximumResponseTime();
	}
}
