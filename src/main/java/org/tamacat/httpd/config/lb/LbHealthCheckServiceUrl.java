/*
 * Copyright (c) 2011, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config.lb;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.PropertyUtils;
import org.tamacat.util.ResourceNotFoundException;
import org.tamacat.util.StringUtils;

/**
 * <p>It is service URL setting of the round robin type load balancer.
 *
 * <pre>ex. url-config.xml
 * {@code
 * <?xml version="1.0" encoding="UTF-8"?>
 * <service-config>
 *   <service host="http://localhost">
 *     <url path="/lb/" type="lb" handler="ReverseHandler">
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
public abstract class LbHealthCheckServiceUrl extends ServiceUrl
		implements HealthCheckSupport<ReverseUrl> {

	static final Log LOG = LogFactory.getLog(LbHealthCheckServiceUrl.class);
	protected static final String MONITOR_PROPERTIES = "monitor.properties";
	protected static final String DEFAULT_URL_KEY = "default.url";
	protected static final String DEFAULT_INTERVAL_KEY = "default.interval";
	protected static final String DEFAULT_TIMEOUT_KEY = "default.timeout";

	protected List<ReverseUrl> reverseUrls = new ArrayList<ReverseUrl>();

	protected Properties monitorProps;
	protected int defaultInterval = 15000;
	protected int defaultTimeout = 5000;
	protected String defaultCheckUrl = "check.html";

	protected LbHealthCheckServiceUrl() {
		loadMonitorConfig();
	}

	protected LbHealthCheckServiceUrl(ServerConfig serverConfig) {
		super(serverConfig);
		loadMonitorConfig();
	}

	protected void loadMonitorConfig() {
		try {
			monitorProps = PropertyUtils.getProperties(MONITOR_PROPERTIES);
			defaultCheckUrl = monitorProps.getProperty(DEFAULT_URL_KEY, defaultCheckUrl);
			defaultInterval = StringUtils.parse(
				monitorProps.getProperty(DEFAULT_INTERVAL_KEY), defaultInterval);
			defaultTimeout = StringUtils.parse(
				monitorProps.getProperty(DEFAULT_TIMEOUT_KEY), defaultTimeout);
		} catch (ResourceNotFoundException e) {
			monitorProps = new Properties();
			monitorProps.setProperty(DEFAULT_URL_KEY, defaultCheckUrl);
			monitorProps.setProperty(DEFAULT_INTERVAL_KEY, String.valueOf(defaultInterval));
			monitorProps.setProperty(DEFAULT_TIMEOUT_KEY, String.valueOf(defaultTimeout));
		}
	}

	public List<ReverseUrl> getReverseUrls() {
		return reverseUrls;
	}

	@Override
	public void setReverseUrl(ReverseUrl reverseUrl) {
		this.reverseUrls.add(reverseUrl);
	}

	@Override
	public abstract ReverseUrl getReverseUrl();

	@Override
	public void addTarget(ReverseUrl target) {
		LOG.debug("add: " + target.getReverse());
		reverseUrls.add(target);
	}

	@Override
	public void removeTarget(ReverseUrl target) {
		LOG.debug("del: " + target.getReverse());
		reverseUrls.remove(target);
	}

	@Override
	public void startHealthCheck() {
		HealthCheckExecutor executor = new HealthCheckExecutor();
		for (ReverseUrl url : reverseUrls) {
			HttpMonitor<ReverseUrl> monitor = new HttpMonitor<>();
			monitor.setHealthCheckTarget(this);
			monitor.setMonitorConfig(getMonitorConfig(url));
			monitor.setTarget(url);
			executor.startMonitor(monitor);
		}
	}

	MonitorConfig getMonitorConfig(ReverseUrl url) {
		String key = url.getServiceUrl().getPath();
		if (key == null) {
			key = "default";
		}
		MonitorConfig config = new MonitorConfig();
		String checkUrl = monitorProps.getProperty(key + ".url");
		URL u = url.getReverse();
		if (checkUrl == null) {
			checkUrl = defaultCheckUrl;
		}
		if (checkUrl.startsWith("http://")==false
			&& checkUrl.startsWith("https://")==false) {
			checkUrl = u != null ? u.toString() + checkUrl : checkUrl;
		}
		config.setUrl(checkUrl);
		config.setInterval(StringUtils.parse(
			monitorProps.getProperty(key + ".interval"), defaultInterval)
		);
		config.setTimeout(StringUtils.parse(
			monitorProps.getProperty(key + ".timeout"), defaultTimeout)
		);
		return config;
	}
}
