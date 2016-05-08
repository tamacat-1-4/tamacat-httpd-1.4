/*
 * Copyright (c) 2013 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import org.apache.http.HeaderIterator;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.TokenIterator;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.util.HeaderUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.StringUtils;

/**
 * The ConnectionReuseStrategy corresponding to keep-alive.
 * <pre>
 * - keepAliveTimeout (default:15000ms)
 * - maxKeepAliveRequests (default:100)
 * </pre>
 * @sinse 1.1
 */
public class KeepAliveConnReuseStrategy extends DefaultConnectionReuseStrategy {
	static final Log LOG = LogFactory.getLog(KeepAliveConnReuseStrategy.class);

	protected static final KeepAliveConnReuseStrategy INSTANCE = new KeepAliveConnReuseStrategy();
	protected static final String HTTP_IN_CONN = "http.in-conn";

	protected ServerConfig serverConfig;

	protected boolean disabledKeepAlive;
	protected int keepAliveTimeout = 15000;
	protected int maxKeepAliveRequests = 100;

	public KeepAliveConnReuseStrategy() {}

	public KeepAliveConnReuseStrategy(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
		setKeepAliveTimeout(serverConfig.getParam("KeepAliveTimeout", 15000));
		setMaxKeepAliveRequests(serverConfig.getParam("MaxKeepAliveRequests", 100));
	}

	/**
	 * Set the Keep-Alive timeout (millisecond).
	 * (default: 15000 ms)
	 * @param keepAliveTimeout
	 */
	public void setKeepAliveTimeout(int keepAliveTimeout) {
		this.keepAliveTimeout = keepAliveTimeout;
	}

	/**
	 * Set the maximum times of keep-alive requests.
	 * (default: 100 requests)
	 * @param maxKeepAliveRequests
	 */
	public void setMaxKeepAliveRequests(int maxKeepAliveRequests) {
		this.maxKeepAliveRequests = maxKeepAliveRequests;
	}

	/**
	 * Set the true, force disabled Keep-Alive.
	 * (default: false)
	 * @param disabledKeepAlive
	 */
	public void setDisabledKeepAlive(boolean disabledKeepAlive) {
		this.disabledKeepAlive = disabledKeepAlive;
	}

	/**
	 * <pre>
	 * 1) disabledKeepAlive:true -> return false.
	 * 2) return super.keepAlive(response, context)
	 * </pre>
	 */
	@Override
	public boolean keepAlive(HttpResponse response, HttpContext context) {
		if (disabledKeepAlive) {
			return false;
		} else {
			boolean result = keepAliveCheck(response, context);
			if (result) {
				return !isKeepAliveTimeout(context);
			}
			return false;
		}
	}

	/**
	 * check the Keep-Alive.
	 * @see DefaultConnectionReuseStrategy#keepAlive(HttpResponse, HttpContext)
	 * @param response
	 * @param context
	 */
	protected boolean keepAliveCheck(HttpResponse response, HttpContext context) {
		Args.notNull(response, "HTTP response");
		Args.notNull(context, "HTTP context");

		// Check for a self-terminating entity. If the end of the entity will
		// be indicated by closing the connection, there is no keep-alive.
		ProtocolVersion ver = response.getStatusLine().getProtocolVersion();
		// default since HTTP/1.1 is persistent, before it was non-persistent
		if (ver.greaterEquals(HttpVersion.HTTP_1_1)) {
			//Connection:Close -> keep-Alive:false
			String close = HeaderUtils.getHeader(response, HTTP.CONN_DIRECTIVE);
			if ("Close".equalsIgnoreCase(close)) {
				debug("Keep-Alive:false (Connection:Close)");
				return false;
			} else {
				debug("Keep-Alive:true (" + ver + ")");
				return true;
			}
		} else {
			String te = HeaderUtils.getHeader(response, HTTP.TRANSFER_ENCODING);
			if (StringUtils.isNotEmpty(te)) {
				if (!HTTP.CHUNK_CODING.equalsIgnoreCase(te)) {
					debug("Keep-Alive:false (Transfer-Encoding: not chunked)");
					return false;
				}
			} else {
				if (canResponseHaveBody(response)) {
					String cl = HeaderUtils.getHeader(response, HTTP.CONTENT_LEN);
					// Do not reuse if not properly content-length delimited
					if (StringUtils.isNotEmpty(cl)) {
						int contentLen = StringUtils.parse(cl, -1);
						if (contentLen < 0) {
							debug("Keep-Alive:false (Content-Length<0 ["+contentLen+"])");
							return false;
						}
					} else {
						debug("Keep-Alive:false (No ontent-Length)");
						return false;
					}
				}
			}

			// Check for the "Connection" header. If that is absent, check for
			// the "Proxy-Connection" header. The latter is an unspecified and
			// broken but unfortunately common extension of HTTP.
			HeaderIterator hit = response.headerIterator(HTTP.CONN_DIRECTIVE);
			if (!hit.hasNext()) hit = response.headerIterator("Proxy-Connection");
			// Experimental usage of the "Connection" header in HTTP/1.0 is
			// documented in RFC 2068, section 19.7.1. A token "keep-alive" is
			// used to indicate that the connection should be persistent.
			// Note that the final specification of HTTP/1.1 in RFC 2616 does not
			// include this information. Neither is the "Connection" header
			// mentioned in RFC 1945, which informally describes HTTP/1.0.
			//
			// RFC 2616 specifies "close" as the only connection token with a
			// specific meaning: it disables persistent connections.
			//
			// The "Proxy-Connection" header is not formally specified anywhere,
			// but is commonly used to carry one token, "close" or "keep-alive".
			// The "Connection" header, on the other hand, is defined as a
			// sequence of tokens, where each token is a header name, and the
			// token "close" has the above-mentioned additional meaning.
			//
			// To get through this mess, we treat the "Proxy-Connection" header
			// in exactly the same way as the "Connection" header, but only if
			// the latter is missing. We scan the sequence of tokens for both
			// "close" and "keep-alive". As "close" is specified by RFC 2068,
			// it takes precedence and indicates a non-persistent connection.
			// If there is no "close" but a "keep-alive", we take the hint.
			if (hit.hasNext()) {
				try {
					TokenIterator ti = createTokenIterator(hit);
					boolean keepalive = false;
					while (ti.hasNext()) {
						final String token = ti.nextToken();
						if (HTTP.CONN_CLOSE.equalsIgnoreCase(token)) {
							debug("Keep-Alive:false (Connection:Close)");
							return false;
						} else if (HTTP.CONN_KEEP_ALIVE.equalsIgnoreCase(token)) {
							// continue the loop, there may be a "close" afterwards
							debug("Keep-Alive:true (Connection:Keep-Alive)");
							keepalive = true;
						}
					}
					if (keepalive) {
						return true;
					}
					// neither "close" nor "keep-alive", use default policy
				} catch (ParseException px) {
					// invalid connection header means no persistent connection
					// we don't have logging in HttpCore, so the exception is lost
					debug("Keep-Alive:false (" + px + ")");
					return false;
				}
			}
			debug("Keep-Alive:false (" + ver + ")");
			return false;
		}
	}

	protected void debug(String message) {
		LOG.debug(message);
	}
	
	/**
	 * Check the Keep-Alive timeout.
	 * @param context
	 * @return true -> timeout
	 */
	protected boolean isKeepAliveTimeout(HttpContext context) {
		boolean timeout = false;
		Object value = context.getAttribute(HTTP_IN_CONN);
		if (value != null && value instanceof ServerHttpConnection) {
			@SuppressWarnings("resource")
			ServerHttpConnection conn = (ServerHttpConnection) value;
			long lastAccessInterval = System.currentTimeMillis() - conn.getLastAccessTime();
			if (lastAccessInterval > keepAliveTimeout) { //timeout
				conn.setSocketTimeout(1);
				debug("keep-alive timeout[" + lastAccessInterval + " > " + keepAliveTimeout + " msec.] - " + conn);
				timeout = true;
			} else if (maxKeepAliveRequests >= 0 && maxKeepAliveRequests <= conn.getMetrics().getRequestCount()) {
				conn.setSocketTimeout(1);
				debug("keep-alive max requests:" + maxKeepAliveRequests + " - " + conn);
				timeout = true;
			} else {
				conn.setSocketTimeout(keepAliveTimeout);
			}
		}
		return timeout;
	}

	protected boolean canResponseHaveBody(final HttpResponse response) {
		int status = response.getStatusLine().getStatusCode();
		return status >= HttpStatus.SC_OK
			&& status != HttpStatus.SC_NO_CONTENT
			&& status != HttpStatus.SC_NOT_MODIFIED
			&& status != HttpStatus.SC_RESET_CONTENT;
	}
}
