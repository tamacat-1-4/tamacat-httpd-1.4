/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Implements the default {@link ThreadFactory}.<br>
 * {@code Thread name: name-$count}
 */
public class DefaultThreadFactory implements ThreadFactory {

	private static final AtomicInteger threadPoolNumber = new AtomicInteger(1);
	private String name = "Thread";

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Thread newThread(final Runnable r) {
		return new Thread(r, name + "-" + threadPoolNumber.incrementAndGet());
	}
}
