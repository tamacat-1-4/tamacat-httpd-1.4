/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core.jmx;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class BasicCounter implements PerformanceCounterMonitor, Serializable {
	static final Log LOG = LogFactory.getLog(BasicCounter.class);

	private static final long serialVersionUID = 6089725451626828983L;

	private static ThreadLocal<Long> time = new ThreadLocal<Long>() {
		@Override
		protected Long initialValue() {
			return System.currentTimeMillis();
		}
	};

	private AtomicInteger activeConnections = new AtomicInteger();
	private AtomicLong accessCount = new AtomicLong();
	private AtomicLong errorCount = new AtomicLong();
	private AtomicLong responseTimes = new AtomicLong();
	private AtomicLong max = new AtomicLong();
	private final Date startedTime = new Date();
	private String path;
	private int port;

	public BasicCounter() {}
	
	public BasicCounter(ServerConfig config) {
		if (config != null) {
			this.port = config.getPort();
		}
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public int getActiveConnections() {
		return activeConnections.get();
	}

	@Override
	public int countUp() {
		time.set(System.currentTimeMillis());
		return activeConnections.incrementAndGet();
	}

	@Override
	public int countDown() {
		Long start = time.get();
		if (start != null) {
			setResponseTime(System.currentTimeMillis() - start);
		}
		time.remove();
		return activeConnections.decrementAndGet();
	}

	@Override
	public void reset() {
		accessCount.set(0);
		errorCount.set(0);
	}

	@Override
	public long getAccessCount() {
		return accessCount.get();
	}

	@Override
	public void resetAccessCount() {
		accessCount.set(0);
	}

	public void access() {
		accessCount.incrementAndGet();
	}

	@Override
	public long getErrorCount() {
		return errorCount.get();
	}

	@Override
	public void resetErrorCount() {
		errorCount.set(0);
	}

	public void error() {
		errorCount.incrementAndGet();
	}

	@Override
	public Date getStartedTime() {
		return startedTime;
	}

	@Override
	public long getAverageResponseTime() {
		return accessCount.get() > 0 ? responseTimes.get() / accessCount.get() : 0;
	}

	@Override
	public long getMaximumResponseTime() {
		return max.get();
	}

	public void setResponseTime(long time) {
		responseTimes.addAndGet(time);
		accessCount.incrementAndGet();
		if (max.get() < time) {
			max.set(time);
		}
	}

	public void register() {
		try {
			String name = "org.tamacat.httpd-"+port+":type=Counter";
			ObjectName oname = new ObjectName(name);
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			server.registerMBean(this, oname);
		} catch (Exception e) {
			LOG.warn(e.getMessage());
			LOG.trace(e);
		}
	}

	public void unregister() {
		try {
			String name = "org.tamacat.httpd-"+port+":type=Counter";
			ObjectName oname = new ObjectName(name);
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			server.unregisterMBean(oname);
		} catch (Exception e) {
			LOG.warn(e.getMessage());
			LOG.trace(e);
		}
	}
}
