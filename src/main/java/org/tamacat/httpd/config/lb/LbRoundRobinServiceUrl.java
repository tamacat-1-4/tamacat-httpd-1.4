/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config.lb;

import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.exception.ServiceUnavailableException;

/**
 * <p>It is service URL setting of the round robin type load balancer.
 *
 * <pre>ex. url-config.xml
 * {@code
 * <?xml version="1.0" encoding="UTF-8"?>
 * <service-config>
 *   <service host="http://localhost">
 *     <url path="/lb/" type="lb" lb-method="RoundRobin" handler="ReverseHandler">
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
public class LbRoundRobinServiceUrl extends LbHealthCheckServiceUrl {

	private int next;

	public LbRoundRobinServiceUrl() {
		loadMonitorConfig();
	}

	public LbRoundRobinServiceUrl(ServerConfig serverConfig) {
		super(serverConfig);
		loadMonitorConfig();
	}

	@Override
	public ReverseUrl getReverseUrl() {
		ReverseUrl reverseUrl = null;
		synchronized (reverseUrls) {
			int size = reverseUrls.size();
			if (size == 0) {
				throw new ServiceUnavailableException();
			} else if (size == 1) {
				reverseUrl = reverseUrls.get(0);
			} else if (size > 1) {
				if (next >= size) {
					next = 0;
				}
				reverseUrl = reverseUrls.get(next);
				next++;
			}
		}
		return reverseUrl;
	}
}
