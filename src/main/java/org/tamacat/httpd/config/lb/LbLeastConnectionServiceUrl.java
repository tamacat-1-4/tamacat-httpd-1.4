/*
 * Copyright (c) 2011, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config.lb;

import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.HttpHost;
import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.core.jmx.PerformanceCounter;
import org.tamacat.httpd.exception.ServiceUnavailableException;

/**
 * <p>It is service URL setting of the least connection type load balancer.
 *
 * <pre>ex. url-config.xml
 * {@code
 * <?xml version="1.0" encoding="UTF-8"?>
 * <service-config>
 *   <service host="http://localhost">
 *     <url path="/lb/" type="lb" lb-method="LeastConnection" handler="ReverseHandler">
 *       <reverse>http://localhost:8080/lb1/</reverse>
 *       <reverse>http://localhost:8088/lb2/</reverse>
 *     </url>
 *   </service>
 * </service-config>}
 * </pre>
 *
 * <pre>ex. monitor.properties
 * {@code
 * default.url=check.html
 * default.interval=15000
 * default.timeout=5000
 *
 * /lb/.url=test/check.html
 * /lb/.interval=60000
 * /lb/.timeout=15000
 * }
 * </pre>
 */
public class LbLeastConnectionServiceUrl extends LbHealthCheckServiceUrl {

	public LbLeastConnectionServiceUrl() {
		loadMonitorConfig();
	}

	public LbLeastConnectionServiceUrl(ServerConfig serverConfig) {
		super(serverConfig);
		loadMonitorConfig();
	}

	@Override
	public void setReverseUrl(ReverseUrl reverseUrl) {
		SortableReverseUrl url = new SortableReverseUrl(reverseUrl);
		url.reset();
		this.reverseUrls.add(url);
	}

	@Override
	public void addTarget(ReverseUrl target) {
		LOG.trace("add: " + target.getReverse());
		setReverseUrl(target);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ReverseUrl getReverseUrl() {
		SortableReverseUrl reverseUrl = null;
		synchronized (reverseUrls) {
			int size = reverseUrls.size();
			if (size == 0) {
				throw new ServiceUnavailableException();
			} else if (size == 1) {
				return reverseUrls.get(0);
			}
			reverseUrl = (SortableReverseUrl)reverseUrls.get(0);
			Collections.sort((List)reverseUrls, new ReverseUrlComparator());
		}
		return reverseUrl;
	}
}

class SortableReverseUrl implements ReverseUrl, PerformanceCounter, Comparable<SortableReverseUrl> {

	ReverseUrl reverseUrl;
	PerformanceCounter counter;

	public SortableReverseUrl(ReverseUrl reverseUrl) {
		if (reverseUrl instanceof PerformanceCounter) {
			counter = (PerformanceCounter) reverseUrl;
		}
		this.reverseUrl = reverseUrl;
	}

	public int getActiveConnections() {
		return counter.getActiveConnections();
	}

	public int countUp() {
		return counter.countUp();
	}

	public int countDown() {
		return counter.countDown();
	}

	public void reset() {
		counter.reset();
	}

	public long getAverageResponseTime() {
		return counter.getAverageResponseTime();
	}

	public long getMaximumResponseTime() {
		return counter.getMaximumResponseTime();
	}

	@Override
	public int compareTo(SortableReverseUrl target) {
		int t = target.getActiveConnections();
		int s = getActiveConnections();
		if (t > s) return -1;
		if (t < s) return 1;
		else return 0;
	}

	@Override
	public ServiceUrl getServiceUrl() {
		return reverseUrl.getServiceUrl();
	}

	@Override
	public URL getHost() {
		return reverseUrl.getHost();
	}

	@Override
	public void setHost(URL host) {
		reverseUrl.setHost(host);
	}

	@Override
	public URL getReverse() {
		return reverseUrl.getReverse();
	}

	@Override
	public void setReverse(URL url) {
		reverseUrl.setReverse(url);
	}

	@Override
	public URL getReverseUrl(String path) {
		return reverseUrl.getReverseUrl(path);
	}

	@Override
	public String getConvertRequestedUrl(String path) {
		return reverseUrl.getConvertRequestedUrl(path);
	}

	@Override
	public InetSocketAddress getTargetAddress() {
		return reverseUrl.getTargetAddress();
	}

	@Override
	public HttpHost getTargetHost() {
		return reverseUrl.getTargetHost();
	}
}

class ReverseUrlComparator implements Comparator<SortableReverseUrl> {

	@Override
	public int compare(SortableReverseUrl o1, SortableReverseUrl o2) {
		return o1.compareTo(o2);
	}
}
