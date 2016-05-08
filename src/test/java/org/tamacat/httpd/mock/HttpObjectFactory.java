package org.tamacat.httpd.mock;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class HttpObjectFactory {

	public static HttpRequest createHttpRequest(String method, String uri) {
		if ("POST".equalsIgnoreCase(method)) {
			return new BasicHttpEntityEnclosingRequest(method, uri);
		} else {
			return new BasicHttpRequest(method, uri);
		}
	}

	public static HttpResponse createHttpResponse(int status, String reason) {
		return new BasicHttpResponse(new ProtocolVersion("HTTP",1,1), status, reason);
	}

	public static HttpResponse createHttpResponse(ProtocolVersion ver, int status, String reason) {
		return new BasicHttpResponse(ver, status, reason);
	}

	public static HttpContext createHttpContext() {
		return new BasicHttpContext();
	}
}
