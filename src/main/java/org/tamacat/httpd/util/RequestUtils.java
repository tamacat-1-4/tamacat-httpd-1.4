/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpInetConnection;
import org.apache.http.HttpRequest;
import org.apache.http.HttpServerConnection;
import org.apache.http.RequestLine;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.core.BasicHttpStatus;
import org.tamacat.httpd.core.RequestParameters;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.util.StringUtils;

public class RequestUtils {

	public static final String X_FORWARDED_FOR = "X-Forwarded-For";
	public static final String REMOTE_ADDRESS = "remote_address";

	static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";
	static final String REQUEST_PARAMETERS_CONTEXT_KEY = "RequestParameters";

	public static String getRequestLine(HttpRequest request) {
		return request.getRequestLine().getMethod() + " "
			+ request.getRequestLine().getUri() + " "
			+ request.getProtocolVersion();
	}
	
	public static RequestLine getRequestLine(RequestLine requestline) {
		String uri = requestline.getUri();
		String path = getRequestPathWithQuery(uri);
		if (uri.equals(path)) {
			return requestline;
		} else {
			return new BasicRequestLine(requestline.getMethod(),
				path.substring(path.indexOf("/"), path.length()),
				requestline.getProtocolVersion());
		}
//		Path path = Paths.get(uri);
//		Path p = path.subpath(0, path.getNameCount());
//		return new BasicRequestLine(requestline.getMethod(),
//				p,
//				requestline.getProtocolVersion());
	}

	/**
	 * Get request absolute URI to Path (With Query)
	 * @param uri
	 */
	public static String getRequestPathWithQuery(final String uri) {
		try {
			if (uri.indexOf("http")==0 && uri.indexOf("://")>0) {
				int idx = uri.indexOf("://");
				String path = uri.substring(idx+3, uri.length());
				if (path.indexOf("/")>0) {
					return path.substring(path.indexOf("/"), path.length());
				}
			}
		} catch (RuntimeException e) {
		}
		return uri;
	}

	public static String getPath(String uri) {
		int index = uri.indexOf('?');
		if (index != -1) {
			uri = uri.substring(0, index);
		} else {
			index = uri.indexOf('#');
			if (index != -1) {
				uri = uri.substring(0, index);
			}
		}
		return uri;
	}
	
	public static String getPath(HttpRequest request) {
		return getPath(request.getRequestLine().getUri());
	}

	public static void setParameter(HttpContext context, String name, String... values) throws IOException {
		RequestParameters parameters = getParameters(context);
		parameters.setParameter(name, values);
	}

	public static void setParameters(HttpRequest request, HttpContext context, String encoding) {
		if (context.getAttribute(REQUEST_PARAMETERS_CONTEXT_KEY) != null) return;
		
		String path = request.getRequestLine().getUri();
		//String path = docsRoot + request.getRequestLine().getUri();
		RequestParameters parameters = getParameters(context);

		if (path.indexOf('?') >= 0) {
			String[] requestParams = path.split("\\?");
			//path = requestParams[0];
			//set request parameters for Custom HttpRequest.
			if (requestParams.length >= 2) {
				String params = requestParams[1];
				String[] param = params.split("&");
				for (String kv : param) {
					String[] p = kv.split("=");
					if (p.length >=2) {
						try {
							parameters.setParameter(p[0], URLDecoder.decode(p[1], encoding));
						} catch (Exception e) {
						}
					}
				}
			}
		}
		if (isEntityEnclosingRequest(request) && ! RequestUtils.isMultipart(request)) {
			HttpEntity entity = getEntity(request);
			if (entity != null) {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()))) {
					String s;
					StringBuilder sb = new StringBuilder();
					while ((s = reader.readLine()) != null) {
						sb.append(s);
					}
					String[] params = sb.toString().split("&");
					for (String param : params) {
						String[] keyValue = param.split("=");
						if (keyValue.length >= 2) {
							try {
								parameters.setParameter(keyValue[0],
									URLDecoder.decode(keyValue[1], encoding));
							} catch (Exception e) {
							}
						}
					}
				} catch (IOException e) {
					throw new HttpException(BasicHttpStatus.SC_BAD_REQUEST, e);
				}
			}
		}
	}

	public static void setParameters(HttpContext context, RequestParameters parameters) {
		context.setAttribute(REQUEST_PARAMETERS_CONTEXT_KEY, parameters);
	}

	/**
	 * Get Request parameters
	 * @since 1.4
	 */
	public static RequestParameters getParameters(HttpRequest request, HttpContext context, String encoding) {
		setParameters(request, context, encoding);
		return getParameters(context);
	}
	
	public static RequestParameters getParameters(HttpContext context) {
		synchronized (context) {
			RequestParameters params = (RequestParameters) context.getAttribute(REQUEST_PARAMETERS_CONTEXT_KEY);
			if (params == null) {
				params = new RequestParameters();
				context.setAttribute(REQUEST_PARAMETERS_CONTEXT_KEY, params);
			}
			return params;
		}
	}

	public static String getParameter(HttpContext context, String name) {
		RequestParameters params = getParameters(context);
		return params != null ? params.getParameter(name) : null;
	}

	public static String[] getParameters(HttpContext context, String name) {
		RequestParameters params = getParameters(context);
		return params != null ? params.getParameters(name) : null;
	}

	public static Set<String> getParameterNames(HttpContext context) {
		RequestParameters params = getParameters(context);
		return params != null ? params.getParameterNames() : null;
	}

	public static HttpConnection getHttpConnection(HttpContext context) {
		return (HttpConnection) context.getAttribute(HttpCoreContext.HTTP_CONNECTION);
	}

	/**
	 * Set the remote IP address to {@code HttpContext}.
	 * @param context
	 * @param conn instance of HttpInetConnection
	 */
	public static void setRemoteAddress(HttpContext context, HttpServerConnection conn) {
		if (conn instanceof HttpInetConnection) {
			InetAddress address = ((HttpInetConnection)conn).getRemoteAddress();
			context.setAttribute(REMOTE_ADDRESS, address);
		}
	}

	/**
	 * Get the remote IP address in {@code HttpContext} or X-Forwarded-For.
	 * @param request
	 * @param context
	 * @param useXFF Using X-Forwarded-For request header.
	 * @return
	 */
	public static String getRemoteIPAddress(HttpRequest request, HttpContext context, boolean useXFF) {
		return getRemoteIPAddress(request, context, useXFF, X_FORWARDED_FOR);
	}
	
	/**
	 * Get the remote IP address in {@code HttpContext} or X-Forwarded-For.
	 * @param request
	 * @param context
	 * @param useForwardHeader Using X-Forwarded-For request header.
	 * @param forwardHeader ("X-Forwarded-For")
	 * @return
	 */
	public static String getRemoteIPAddress(HttpRequest request, HttpContext context, boolean useForwardHeader, String forwardHeader) {
		String ip = null;
		if (useForwardHeader) {
			ip = HeaderUtils.getHeader(request, StringUtils.isNotEmpty(forwardHeader)? forwardHeader : X_FORWARDED_FOR);
			if (StringUtils.isNotEmpty(ip) && ip.indexOf(",")>=0) {
				String[] address = StringUtils.split(ip, ",");
				if (address.length > 0) {
					return address[0];
				}
			}
		}
		if (StringUtils.isEmpty(ip)) {
			ip = getRemoteIPAddress(context);
		}
		return ip != null ? ip : "";
	}
	
	/**
	 * Get the remote IP address in {@code HttpContext}.
	 * @param context
	 * @return
	 */
	public static String getRemoteIPAddress(HttpContext context) {
		InetAddress address = (InetAddress) context.getAttribute(REMOTE_ADDRESS);
		if (address != null) return address.getHostAddress();
		else return "";
	}

	public static boolean isRemoteIPv6Address(HttpContext context) {
		InetAddress address = (InetAddress) context.getAttribute(REMOTE_ADDRESS);
		if (address != null && address instanceof Inet6Address) {
			return true;
		} else {
			return false;
		}
	}

	public static String getRequestHostURL(
			HttpRequest request, HttpContext context) {
		URL host = getRequestURL(request, context);
		return host != null ? host.getProtocol()
				+ "://" + host.getAuthority() : null;
	}

	public static String getRequestHostURL(
			HttpRequest request, HttpContext context, ServiceUrl url) {
		URL host = getRequestURL(request, context, url);
		return host != null ? host.getProtocol()
				+ "://" + host.getAuthority() : null;
	}

	public static URL getRequestURL(HttpRequest request, HttpContext context) {
		return getRequestURL(request, context, null);
	}

	public static URL getRequestURL(HttpRequest request, HttpContext context, ServiceUrl url) {
		String protocol = "http";
		String hostName = null;
		int port = -1;
		Header hostHeader = request.getFirstHeader(HTTP.TARGET_HOST);
		if (hostHeader != null) {
			hostName = hostHeader.getValue();
			if (hostName != null && hostName.indexOf(':') >= 0) {
				String[] hostAndPort = hostName.split(":");
				if (hostAndPort.length >= 2) {
					hostName = hostAndPort[0];
					port = StringUtils.parse(hostAndPort[1],-1);
				}
			}
		}
		if (url != null) {
			URL configureHost = url.getHost();
			if (configureHost != null) {
				protocol = configureHost.getProtocol();
				if (hostName == null) {
					hostName = configureHost.getHost();
				}
			}
			if (url.getServerConfig().useHttps()) {
				protocol = "https";
			}
			if (hostName != null && hostName.indexOf(':') >= 0) {
				String[] hostAndPort = hostName.split(":");
				if (hostAndPort.length >= 2) {
					hostName = hostAndPort[0];
					port = StringUtils.parse(hostAndPort[1],-1);
				}
			} else {
				port = url.getServerConfig().getPort();
			}
			if (context != null) {
				HttpConnection con = getHttpConnection(context);
				if (con instanceof HttpInetConnection) {
					port = ((HttpInetConnection)con).getLocalPort();
					InetAddress addr = ((HttpInetConnection)con).getLocalAddress();
					if (hostName == null && addr != null) {
						hostName = addr.getHostName();
					}
				}
			}
		}
		if (("http".equalsIgnoreCase(protocol) && port == 80)
			|| ("https".equalsIgnoreCase(protocol) && port == 443)){
			port = -1;
		}
		if (hostName != null) {
			try {
				return new URL(protocol, hostName, port,
					request.getRequestLine().getUri());
			} catch (MalformedURLException e) {
			}
		}
		return null;
	}

	/**
	 * UnsupportedEncodingException -> value returns.
	 * @param value
	 * @param encoding
	 * @return
	 */
	static String decode(String value, String encoding) {
		String decode = null;
		try {
			decode = URLDecoder.decode(value, encoding);
		} catch (UnsupportedEncodingException e) {
			decode = value;
		}
		return decode;
	}

	public static boolean isEntityEnclosingRequest(HttpRequest request) {
		return request != null && request instanceof HttpEntityEnclosingRequest;
	}

	public static HttpEntity getEntity(HttpRequest request) {
		if (isEntityEnclosingRequest(request)) {
			return ((HttpEntityEnclosingRequest)request).getEntity();
		} else {
			return null;
		}
	}

	public static InputStream getInputStream(HttpRequest request) throws IOException {
		HttpEntity entity = getEntity(request);
		return entity != null? entity.getContent() : null;
	}

	public static boolean isMultipart(HttpRequest request) {
		if ("post".equalsIgnoreCase(request.getRequestLine().getMethod())) {
			return HeaderUtils.isMultipart(
				HeaderUtils.getHeader(request, HTTP.CONTENT_TYPE));
		}
		return false;
	}

	public static String getPathPrefix(HttpRequest request) {
		String path = request.getRequestLine().getUri();
		int idx = path.lastIndexOf("/");
		if (idx >=0) {
			return path.substring(0, idx) + "/";
		}
		return path;
	}

	/**
	 *
	 * @param context
	 * @since 1.1
	 */
	public static HttpRequest getHttpRequest(HttpContext context) {
		return (HttpRequest) context.getAttribute(HttpCoreContext.HTTP_REQUEST);
	}
}
