/*
 * Copyright (c) 2014, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config.lb;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.tamacat.httpd.util.DefaultThreadFactory;

public class HealthCheckExecutor {

	public HealthCheckExecutor() {
	}

	private boolean isStarted;
	ScheduledExecutorService executor;

	int initialDelay = 5;
	int delay = 15;
	ScheduledFuture<?> future;

	public void startMonitor(HttpMonitor<?> monitor) {
		DefaultThreadFactory threadFactory = new DefaultThreadFactory();
		threadFactory.setName("Monitor");
		executor = Executors.newSingleThreadScheduledExecutor(threadFactory);
		future = executor.scheduleWithFixedDelay(monitor, initialDelay, delay, TimeUnit.SECONDS);
		isStarted = true;
	}

	public void stopMonitor() {
		if (future != null) future.cancel(true);
		if (executor != null) executor.shutdown();
		isStarted = false;
	}

	public String getStatus() {
		if (isStarted) {
			return "Started";
		} else {
			return "Stopped";
		}
	}
}
