/*
 * Copyright (c) 2009 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import javax.net.ssl.SSLException;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpConnection;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpRequestFactory;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.core.Worker;
import org.tamacat.httpd.core.jmx.BasicCounter;
import org.tamacat.io.RuntimeIOException;
import org.tamacat.log.DiagnosticContext;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.ExceptionUtils;

/**
 * <p>This class is a worker thread for multi thread server.
 */
public class DefaultWorker implements Worker {
	static final Log LOG = LogFactory.getLog(DefaultWorker.class);
	static final DiagnosticContext DC = LogFactory.getDiagnosticContext(LOG);

	static final String HTTP_IN_CONN = "http.in-conn";
	static final BasicCounter COUNTER = new BasicCounter();
	
	static {
		COUNTER.register();
	}
	
	protected ServerConfig serverConfig;
	protected HttpService httpService;
	protected Socket socket;
	protected ServerHttpConnection conn;
	protected HttpRequestFactory httpRequestFactory;
	

	public DefaultWorker() {
		httpRequestFactory = new StandardHttpRequestFactory();
	}

	public DefaultWorker(ServerConfig serverConfig, HttpService httpService, HttpRequestFactory httpRequestFactory,Socket socket) {
		this.httpRequestFactory = httpRequestFactory;
		setHttpService(httpService);
		setServerConfig(serverConfig);
		setSocket(socket);
	}
	
	@Override
	public void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
		this.conn = new ServerHttpConnection(serverConfig.getSocketBufferSize(), httpRequestFactory);
	}

	@Override
	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}

	@Override
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			this.conn.bind(socket);
			countUp();
			LOG.debug("bind - " + conn);
			HttpConnectionMetrics metrics = this.conn.getMetrics();
			while (Thread.interrupted()==false) {
				HttpContext context = new BasicHttpContext();
				if (!conn.isOpen()) {
					break;
				} else {
					//Bind server connection objects to the execution context
					context.setAttribute(HTTP_IN_CONN, conn);
				}
				if (LOG.isDebugEnabled()){
					LOG.debug("count:" + metrics.getRequestCount() +  " - " + conn);
				}
				this.httpService.handleRequest(conn, context);
				DC.remove(); //delete Logging context.
			}
		} catch (Exception e) {
			handleException(e);
		} finally {
			shutdown(conn);
			countDown();
		}
	}
	
	protected void handleException(Exception e) {
		//Connection reset by peer: socket write error
		if (e instanceof SSLException || e instanceof SocketException) {
			LOG.debug(e.getClass() + ": " + e.getMessage() + " - " + conn);
		} else if (e instanceof ConnectionClosedException) {
			LOG.debug("client closed connection. - " + conn);
		} else if (e instanceof SocketTimeoutException) {
			LOG.debug("timeout >> close connection. - " + conn);
		} else if (e instanceof RuntimeIOException) {
			//SocketException: Broken pipe
			LOG.warn(e.getClass() + ": " + e.getMessage() + " - " + conn);
			LOG.trace(ExceptionUtils.getStackTrace(e));
		} else {
			LOG.error(e.getClass() + ": " + e.getMessage() + " - " + conn);
			LOG.debug(ExceptionUtils.getStackTrace(e));
		}
	}

	protected boolean isClosed() {
		return socket.isClosed();
	}

	protected void shutdown(HttpConnection conn) {
		try {
			if (conn != null) {
				String connString = conn.toString();
				conn.shutdown();
				LOG.debug("server conn shutdown. - " + connString);
			}
		} catch (IOException ignore) {
		} finally {
			DC.remove();
		}
	}
	
	protected void countUp() {
		int active = COUNTER.countUp();
		LOG.trace("active: "+active);
	}

	protected void countDown() {
		int active = COUNTER.countDown();
		LOG.trace("active: "+active);
	}
}
