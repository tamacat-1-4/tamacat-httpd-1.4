/*
 * Copyright (c) 2015 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.handler;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;
import org.tamacat.httpd.config.HttpProxyConfig;
import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.core.BackEndKeepAliveConnReuseStrategy;
import org.tamacat.httpd.core.BasicHttpStatus;
import org.tamacat.httpd.core.jmx.PerformanceCounter;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.httpd.filter.LocationRedirectResponseInterceptor;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.httpd.util.ReverseUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * The {@link HttpHandler} for reverse proxy using HttpClient.
 * A proxy and https are being supported in the access course to the backend server from a reverse proxy.
 * 
 * server.roperties (default value)
 * <pre>
 * BackEndBacklogSize=0
 * BackEndSocketBufferSize=8192
 * BackEndSocketTimeout=30000
 * BackEndConnectionTimeout=5000
 * BackEndMaxPerRoute=20
 * BackEndMaxConnectons=100
 * </pre>
 */
public class HCReverseProxyHandler extends AbstractHttpHandler {

	static final Log LOG = LogFactory.getLog(HCReverseProxyHandler.class);

	protected static final String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";

	protected String proxyAuthorizationHeader = "X-ReverseProxy-Authorization";
	protected String proxyOrignPathHeader = "X-ReverseProxy-Origin-Path"; // v1.1
	protected HttpProxyConfig proxyConfig = new HttpProxyConfig();
	protected HttpClientBuilder builder = HttpClients.custom();
	protected boolean useForwardHeader;
	protected String forwardHeader = "X-Forwarded-For";
	
	@Override
	public void setServiceUrl(ServiceUrl serviceUrl) {
		super.setServiceUrl(serviceUrl);
		setParseRequestParameters(false);		
		SocketConfig socketConfig = SocketConfig.custom()
			.setBacklogSize(getParam("BackEndBacklogSize", 0)).build();
		
		ConnectionConfig connConfig = ConnectionConfig.custom()
			.setBufferSize(getParam("BackEndSocketBufferSize", 8192))
			.build();
		
		RequestConfig requestConfig = RequestConfig.custom()
			.setSocketTimeout(getParam("BackEndSocketTimeout", 30000))
			.setConnectTimeout(getParam("BackEndConnectionTimeout", 5000))
			.build();
		
		builder.setDefaultSocketConfig(socketConfig)
			.setDefaultConnectionConfig(connConfig)
			.setDefaultRequestConfig(requestConfig)
			.setRequestExecutor(new HttpRequestExecutor())
			.setConnectionReuseStrategy(new BackEndKeepAliveConnReuseStrategy(serviceUrl.getServerConfig()))
			.setMaxConnPerRoute(getParam("BackEndMaxPerRoute", 20))
			.setMaxConnTotal(getParam("BackEndMaxConnectons", 100))
			.setRedirectStrategy(new DefaultRedirectStrategy())
			.addInterceptorFirst(new LocationRedirectResponseInterceptor());
	}

	protected <T>T getParam(String name, T defaultValue) {
		return serviceUrl.getServerConfig().getParam(name, defaultValue);
	}
	
	@Override
	public void doRequest(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		//Set the X-Forwarded Headers.
		ReverseUtils.setXForwardedFor(request, context, useForwardHeader, forwardHeader);
		ReverseUtils.setXForwardedHost(request);

		ReverseUrl reverseUrl = getReverseUrl(context);

		//Access Backend server.
		HttpResponse targetResponse = forwardRequest(request, response, context, reverseUrl);

		ReverseUtils.copyHttpResponse(targetResponse, response);
		ReverseUtils.rewriteStatusLine(request, response);
		ReverseUtils.rewriteContentLocationHeader(request, response, reverseUrl);

		ReverseUtils.rewriteServerHeader(response, reverseUrl);
		
		//Location Header convert.
		ReverseUtils.rewriteLocationHeader(request, response, reverseUrl);

		//Set-Cookie Header convert.
		ReverseUtils.rewriteSetCookieHeader(request, response, reverseUrl);

		//Set the entity and response headers from targetResponse.
		response.setEntity(targetResponse.getEntity());
	}

	/**
	 * Request forwarding to backend server.
	 */
	protected HttpResponse forwardRequest(HttpRequest request, HttpResponse response, 
			HttpContext context, ReverseUrl reverseUrl) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(">> " + RequestUtils.getRequestLine(request));
		}

		if (reverseUrl == null) {
			throw new ServiceUnavailableException("reverseUrl is null.");
		}
		try {
			context.setAttribute("reverseUrl", reverseUrl);

			HttpClientContext reverseContext = HttpClientContext.create();//(context);
			reverseContext.setAttribute("reverseUrl", reverseUrl);
			
			ReverseHttpRequest targetRequest = ReverseHttpRequestFactory
					.getInstance(request, response, reverseContext, reverseUrl);
			
			targetRequest.setHeader(proxyOrignPathHeader, serviceUrl.getPath()); // v1.1
			
			//forward remote user.
			ReverseUtils.setReverseProxyAuthorization(targetRequest, context, proxyAuthorizationHeader);
			try {
				countUp(reverseUrl, context);
				CloseableHttpResponse targetResponse = builder.build().execute(
						reverseUrl.getTargetHost(), targetRequest, reverseContext);
				LocationRedirectResponseInterceptor.checkRedirect(targetResponse, reverseContext);
				return targetResponse;
			} finally {
				countDown(reverseUrl, context);
			}
		} catch (SocketException e) {
			throw new ServiceUnavailableException(
					BasicHttpStatus.SC_GATEWAY_TIMEOUT.getReasonPhrase()
							+ " URL=" + reverseUrl.getReverse());
		} catch (RuntimeException e) {
			handleException(request, response, e);
			return response;
		} catch (ConnectTimeoutException e) {
			throw new HttpException(BasicHttpStatus.SC_GATEWAY_TIMEOUT, e);
		} catch (Exception e) {
			handleException(request, response, e);
			return response;
		}
	}
	
	public void addHttpRequestInterceptor(HttpRequestInterceptor interceptor) {
		builder.addInterceptorLast(interceptor);
	}

	public void addHttpResponseInterceptor(HttpResponseInterceptor interceptor) {
		builder.addInterceptorLast(interceptor);
	}

	/**
	 * Set the header name of Reverse Proxy Authorization. default: "X-ReverseProxy-Authorization"
	 * @param proxyAuthorizationHeader
	 */
	public void setProxyAuthorizationHeader(String proxyAuthorizationHeader) {
		this.proxyAuthorizationHeader = proxyAuthorizationHeader;
	}

	/**
	 * Set the header name of Reverse Proxy Origin Path. default: "X-ReverseProxy-Origin-Path"
	 * @param proxyOrignPathHeader
	 * @since 1.1
	 */
	public void setProxyOrignPathHeader(String proxyOrignPathHeader) {
		this.proxyOrignPathHeader = proxyOrignPathHeader;
	}

	@Override
	protected HttpEntity getEntity(String html) {
		try {
			StringEntity entity = new StringEntity(html, encoding);
			entity.setContentType(DEFAULT_CONTENT_TYPE);
			return entity;
		} catch (Exception e) {
			// UnsupportedEncodingException or UnsupportedCharsetException
			return null;
		}
	}

	@Override
	protected HttpEntity getFileEntity(File file) {
		ContentType type = ContentType.DEFAULT_TEXT;
		try {
			type = ContentType.create(getContentType(file));
		} catch (Exception e) {
		}
		return new FileEntity(file, type);
	}

	/**
	 * Get the ReverseUrl
	 * @since 1.2
	 */
	protected ReverseUrl getReverseUrl(HttpContext context) {
		ReverseUrl reverse = serviceUrl.getReverseUrl();
		
		if ("https".equalsIgnoreCase(reverse.getTargetHost().getSchemeName())) {
			builder.setSSLSocketFactory(ReverseUtils.createSSLSocketFactory("TLS"));
		}
		return reverse;
	}

	public void setHttpProxyConfig(HttpProxyConfig proxyConfig) {
		this.proxyConfig = proxyConfig;
		if (proxyConfig.isDirect() == false) {
			DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxyConfig.getProxyHttpHost());
			builder.setRoutePlanner(routePlanner);
		}
	}
	
	/**
	 * The number of threads under processing is counted up.
	 * @since 1.2
	 */
	protected void countUp(ReverseUrl reverseUrl, HttpContext context) {
		if (reverseUrl instanceof PerformanceCounter) {
			((PerformanceCounter) reverseUrl).countUp();
		}
	}

	/**
	 * The number of threads under processing is counted down.
	 * @since 1.2
	 */
	protected void countDown(ReverseUrl reverseUrl, HttpContext context) {
		if (reverseUrl instanceof PerformanceCounter) {
			((PerformanceCounter) reverseUrl).countDown();
		}
	}
	
	public void setUseForwardHeader(boolean forwardHeader) {
		this.useForwardHeader = forwardHeader;
	}

	public void setForwardHeader(String forwardHeader) {
		this.forwardHeader = forwardHeader;
	}
}
