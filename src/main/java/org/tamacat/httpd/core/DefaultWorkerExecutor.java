/*
 * Copyright (c) 2013 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpRequestFactory;
import org.apache.http.protocol.HttpService;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.util.DefaultThreadFactory;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * <p>The factory class of {@link ExecutorService}.
 *  Support {@link Executors#newFixedThreadPool} or {@link Executors#newCachedThreadPool}.
 * @since 1.1
 */
public class DefaultWorkerExecutor implements WorkerExecutor {
	static final Log LOG = LogFactory.getLog(DefaultWorkerExecutor.class);

	protected ServerConfig serverConfig;
	protected HttpService httpService;

	protected int maxThreads;

	protected ExecutorService executorService;
	protected DefaultThreadFactory threadFactory = new DefaultThreadFactory();
	protected HttpRequestFactory httpRequestFactory;
	
	public DefaultWorkerExecutor() {
		this(new StandardHttpRequestFactory());
	}

	protected DefaultWorkerExecutor(HttpRequestFactory httpRequestFactory) {
		this.httpRequestFactory = httpRequestFactory;
	}

	public void setHttpRequestFactory(HttpRequestFactory httpRequestFactory) {
		this.httpRequestFactory = httpRequestFactory;
	}

	public void execute(Socket socket) throws IOException {
		ExecutorService executors = getExecutorService();
		executors.execute(createWorker(socket));
	}

	protected Worker createWorker(Socket socket) {
		return new DefaultWorker(serverConfig, httpService, httpRequestFactory, socket);
	}

	@Override
	public void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
		//set the maximun worker threads.
		maxThreads = serverConfig.getMaxThreads();
		LOG.info(serverConfig.getParam("ServerName")+":"+serverConfig.getPort() + " MaxServerThreads: " + maxThreads);
		String name = serverConfig.getParam("WorkerThreadName", "httpd");
		threadFactory.setName(name);
	}

	@Override
	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}

	/**
	 * <p>returns a fixed thread pool or cached thread pool.
	 * If fixed number of maximum threads, It use the fixed thread pool
	 * of {@code ExecutorService}.
	 * @return {@link ThreadPoolExecutor}
	 */
	protected ExecutorService getExecutorService() {
		if (executorService == null) {
			if (maxThreads > 0) {
				executorService = Executors.newFixedThreadPool(maxThreads, threadFactory);
			} else {
				executorService = Executors.newCachedThreadPool(threadFactory);
			}
		}
		return executorService;
	}

	@Override
	public void shutdown() {
		if (executorService != null) {
			executorService.shutdown();
		}
	}
}
