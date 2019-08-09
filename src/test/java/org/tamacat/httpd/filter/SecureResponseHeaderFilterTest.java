package org.tamacat.httpd.filter;

import static org.junit.Assert.*;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;
import org.tamacat.httpd.util.HeaderUtils;

public class SecureResponseHeaderFilterTest {

	@Test
	public void testAfterResponse() {
		HttpRequest request = createHttpRequest("GET", "/");
		HttpResponse response = createHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
		HttpContext context = createHttpContext();
		
		SecureResponseHeaderFilter filter = new SecureResponseHeaderFilter();
		filter.afterResponse(request, response, context);
		//for (Header h : response.getAllHeaders()) {
		//	System.out.println(h);
		//}
		assertEquals("DENY", HeaderUtils.getHeader(response, "X-Frame-Options"));
		assertEquals("nosniff", HeaderUtils.getHeader(response, "X-Content-Type-Options"));
		assertEquals("1; mode=block", HeaderUtils.getHeader(response, "X-XSS-Protection"));
		assertEquals("Thu, 01 Jan 1970 00:00:00 GMT", HeaderUtils.getHeader(response, HttpHeaders.EXPIRES));
		assertEquals("no-store, no-cache, must-revalidate, post-check=0, pre-check=0", HeaderUtils.getHeader(response, HttpHeaders.CACHE_CONTROL));
		assertEquals("no-cache", HeaderUtils.getHeader(response, HttpHeaders.PRAGMA));
	}
	
	@Test
	public void testAfterResponse2() {
		HttpRequest request = createHttpRequest("GET", "/");
		HttpResponse response = createHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
		response.addHeader("X-Frame-Options","SAMEORIGIN");
		response.addHeader(HttpHeaders.CACHE_CONTROL, "private, no-store, no-cache, must-revalidate, post-check=0, pre-check=0");
		response.addHeader(HttpHeaders.EXPIRES, "Thu, 19 Nov 1981 08:52:00 GMT");
		response.addHeader("X-XSS-Protection", "0");
		HttpContext context = createHttpContext();
		
		SecureResponseHeaderFilter filter = new SecureResponseHeaderFilter();
		filter.afterResponse(request, response, context);
		//for (Header h : response.getAllHeaders()) {
		//	System.out.println(h);
		//}
		assertEquals("SAMEORIGIN", HeaderUtils.getHeader(response, "X-Frame-Options"));
		assertEquals("nosniff", HeaderUtils.getHeader(response, "X-Content-Type-Options"));
		assertEquals("0", HeaderUtils.getHeader(response, "X-XSS-Protection"));
		assertEquals("Thu, 19 Nov 1981 08:52:00 GMT", HeaderUtils.getHeader(response, HttpHeaders.EXPIRES));
		assertEquals("private, no-store, no-cache, must-revalidate, post-check=0, pre-check=0", HeaderUtils.getHeader(response, HttpHeaders.CACHE_CONTROL));
		assertEquals("no-cache", HeaderUtils.getHeader(response, HttpHeaders.PRAGMA));
	}

	@Test
	public void testSetFramesOptions() {
		HttpRequest request = createHttpRequest("GET", "/");
		HttpResponse response = createHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
		HttpContext context = createHttpContext();
		
		SecureResponseHeaderFilter filter = new SecureResponseHeaderFilter();
		filter.setFrameOptions("SAMEORIGIN");
		filter.afterResponse(request, response, context);
		assertEquals("SAMEORIGIN", HeaderUtils.getHeader(response, "X-Frame-Options"));
	}

	@Test
	public void testSetContentTypeOptions() {
		HttpRequest request = createHttpRequest("GET", "/");
		HttpResponse response = createHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
		HttpContext context = createHttpContext();
		
		SecureResponseHeaderFilter filter = new SecureResponseHeaderFilter();
		filter.setContentTypeOptions("");
		filter.afterResponse(request, response, context);
		assertEquals(null, HeaderUtils.getHeader(response, "X-Content-Type-Options"));
	}

	@Test
	public void testSetXssProtection() {
		HttpRequest request = createHttpRequest("GET", "/");
		HttpResponse response = createHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
		HttpContext context = createHttpContext();
		
		SecureResponseHeaderFilter filter = new SecureResponseHeaderFilter();
		filter.setXssProtection("0");
		filter.afterResponse(request, response, context);
		assertEquals("0", HeaderUtils.getHeader(response, "X-XSS-Protection"));
	}

	@Test
	public void testSetExpires() {
		HttpRequest request = createHttpRequest("GET", "/");
		HttpResponse response = createHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
		HttpContext context = createHttpContext();
		
		SecureResponseHeaderFilter filter = new SecureResponseHeaderFilter();
		filter.setExpires("Thu, 19 Nov 1981 08:52:00 GMT");
		filter.afterResponse(request, response, context);
		assertEquals("Thu, 19 Nov 1981 08:52:00 GMT", HeaderUtils.getHeader(response, HttpHeaders.EXPIRES));
	}

	@Test
	public void testSetCacheControl() {
		HttpRequest request = createHttpRequest("GET", "/");
		HttpResponse response = createHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
		HttpContext context = createHttpContext();
		
		SecureResponseHeaderFilter filter = new SecureResponseHeaderFilter();
		filter.setCacheControl("no-store");
		filter.afterResponse(request, response, context);
		assertEquals("no-store", HeaderUtils.getHeader(response, HttpHeaders.CACHE_CONTROL));
	}

	@Test
	public void testSetPragma() {
		HttpRequest request = createHttpRequest("GET", "/");
		HttpResponse response = createHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
		HttpContext context = createHttpContext();
		
		SecureResponseHeaderFilter filter = new SecureResponseHeaderFilter();
		filter.setPragma("no-cache");
		filter.afterResponse(request, response, context);
		assertEquals("no-cache", HeaderUtils.getHeader(response, HttpHeaders.PRAGMA));
		
		filter.setPragma("");
		response = createHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
		filter.afterResponse(request, response, context);
		assertEquals(null, HeaderUtils.getHeader(response, HttpHeaders.PRAGMA));
	}
	
	@Test
	public void testContentType() throws Exception {
		HttpRequest request = createHttpRequest("GET", "/");
		HttpResponse response = createHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
		response.setEntity(new ByteArrayEntity("TEST".getBytes()));
		HttpContext context = createHttpContext();
		//System.out.println(response.getEntity().getContentType());
		SecureResponseHeaderFilter filter = new SecureResponseHeaderFilter();
		filter.afterResponse(request, response, context);
		assertEquals("text/html; charset=UTF-8", HeaderUtils.getHeader(response, HttpHeaders.CONTENT_TYPE));
	}

	@Test
	public void testContentTypeJSON() throws Exception {
		HttpRequest request = createHttpRequest("GET", "/test");
		HttpResponse response = createHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
		response.setEntity(new StringEntity("{}", ContentType.APPLICATION_JSON));
		HttpContext context = createHttpContext();
		//System.out.println(response.getEntity().getContentType());
		SecureResponseHeaderFilter filter = new SecureResponseHeaderFilter();
		filter.afterResponse(request, response, context);
		assertEquals(null, HeaderUtils.getHeader(response, HttpHeaders.CONTENT_TYPE));
		assertEquals("application/json; charset=UTF-8", response.getEntity().getContentType().getValue());
	}
	
	@Test
	public void testContentType200() throws Exception {
		HttpRequest request = createHttpRequest("GET", "/font.woff2");
		HttpResponse response = createHttpResponse(HttpVersion.HTTP_1_1, 200, "OK");
		response.setEntity(new ByteArrayEntity("TEST".getBytes()));
		//System.out.println(response.getEntity().getContentType());
		HttpContext context = createHttpContext();
		
		SecureResponseHeaderFilter filter = new SecureResponseHeaderFilter();
		filter.afterResponse(request, response, context);
		assertEquals("font/woff2", HeaderUtils.getHeader(response, HttpHeaders.CONTENT_TYPE));
	}

	@Test
	public void testContentType302() throws Exception {
		HttpRequest request = createHttpRequest("GET", "/font.woff2");
		HttpResponse response = createHttpResponse(HttpVersion.HTTP_1_1, 302, "Found");

		HttpContext context = createHttpContext();
		
		SecureResponseHeaderFilter filter = new SecureResponseHeaderFilter();
		filter.afterResponse(request, response, context);
		assertEquals(null, HeaderUtils.getHeader(response, HttpHeaders.CONTENT_TYPE));
	}
	
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
