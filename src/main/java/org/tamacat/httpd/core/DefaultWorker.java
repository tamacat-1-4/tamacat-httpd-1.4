/*
 * Copyright (c) 2009 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

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
	static final String HTTP_OUT_CONN = "http.out-conn";
	static final String HTTP_CONN_KEEPALIVE = "http.proxy.conn-keepalive";
	static final BasicCounter COUNTER = new BasicCounter();
	
	static {
		COUNTER.register();
	}
	
	protected ServerConfig serverConfig;
	protected HttpService httpService;
	protected Socket socket;
	protected ServerHttpConnection conn;
	protected HttpRequestFactory httpRequestFactory;
	protected boolean workerThreadClientConnectionClose;
	

	public DefaultWorker() {
		httpRequestFactory = new StandardHttpRequestFactory();
	}

	public DefaultWorker(ServerConfig serverConfig, HttpService httpService, HttpRequestFactory httpRequestFactory,Socket socket) {
		this.httpRequestFactory = httpRequestFactory;
		setHttpService(httpService);
		setServerConfig(serverConfig);
		setSocket(socket);
	}
	
	public boolean isWorkerThreadClientConnectionClose() {
		return "true".equalsIgnoreCase(serverConfig.getParam("WorkerThreadClientConnectionClose", "true"));
	}
	
	@Override
	public void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
		this.conn = new ServerHttpConnection(serverConfig.getSocketBufferSize(), httpRequestFactory);
		//serverConfig.getParam("WorkerThreadName", "httpd");
		workerThreadClientConnectionClose = isWorkerThreadClientConnectionClose();
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
		HttpContext parent = new BasicHttpContext(); //for client connection keep-alive.
		try {
			this.conn.bind(socket);
			countUp();
			LOG.debug("bind - " + conn);
			HttpConnectionMetrics metrics = this.conn.getMetrics();
			while (Thread.interrupted()==false) {
				HttpContext context = new BasicHttpContext();
				if (!conn.isOpen()) {
					//Shutdown KeepAlive Backend HttpConnections. (Connection Closed)
					shutdownClients(getClientHttpConnections(parent));
					break;
				} else {
					//Bind server connection objects to the execution context
					context.setAttribute(HTTP_IN_CONN, conn);
					//reuse and remove client connections (using reverse proxy keep-alive)
					context.setAttribute(HTTP_OUT_CONN, parent.removeAttribute(HTTP_OUT_CONN));
				}
				if (LOG.isDebugEnabled()){
					LOG.debug("count:" + metrics.getRequestCount() +  " - " + conn);
				}
				this.httpService.handleRequest(conn, context);
				
				//Reuse ClientHttpConnections for ReverseProxy Backend KeepAlive
				Collection<ClientHttpConnection> clientConns = getClientHttpConnections(context); //set from handleRequest (reverse proxy)
				if (clientConns.size() > 0) {
					if (isEnabledClientConnectionKeepAlive()) {
						parent.setAttribute(HTTP_OUT_CONN, context.getAttribute(HTTP_OUT_CONN));
						if (LOG.isDebugEnabled()){
							for (ClientHttpConnection clientConn : clientConns) { 
								HttpConnectionMetrics clientConnMetrics = clientConn.getMetrics();
								if (clientConnMetrics != null) {
									LOG.debug("client conn count:" + clientConnMetrics.getRequestCount() + " - " + conn);
								}
							}
						}
					} else {
						//Shutdown Backend HttpConnections. (Disabled KeepAlive)
						shutdownClients(clientConns);
					}
				}
				DC.remove(); //delete Logging context.
			}
		} catch (Exception e) {
			handleException(e);
		} finally {
			shutdown(conn);
			countDown();
		}
	}
	
	@SuppressWarnings("unchecked")
	protected Collection<ClientHttpConnection> getClientHttpConnections(HttpContext context) {
		Object value = context.getAttribute(HTTP_OUT_CONN);
		if (conn != null && value != null && value instanceof Map<?,?>) {
			return ((Map<String, ClientHttpConnection>)value).values();
		}
		return new ArrayList<>();
	}
	
	protected boolean isEnabledClientConnectionKeepAlive() {
		if (workerThreadClientConnectionClose || conn == null || !conn.isOpen()) {
			return false;
		} else {
			return true;
		}
	}
	
	protected boolean isClientConnectionKeepAlive(ClientHttpConnection clientConnn, HttpContext context) {
		if (workerThreadClientConnectionClose || conn == null || !conn.isOpen() || clientConnn == null || !clientConnn.isOpen()) {
			return false;
		}
		Object value = context.getAttribute(HTTP_CONN_KEEPALIVE);
		if (value != null && value instanceof Boolean) {
			Boolean result = (Boolean) value;
			return result.booleanValue();
		} else {
			return false;
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

	protected void shutdownClients(Collection<ClientHttpConnection> clientConns) {
		if (clientConns != null) {
			for (ClientHttpConnection clientConn : clientConns) {
				shutdownClient(clientConn);
			}
		}
	}
	
	protected void shutdownClient(ClientHttpConnection clientConn) {
		String connString = clientConn.toString();
		try {
			clientConn.close();
			LOG.debug("client conn closed. - " + connString);
			
			clientConn.shutdown();
			LOG.debug("client conn shutdown. - " + connString);
		} catch (IOException ignore) {
		}
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
