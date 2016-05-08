package org.tamacat.httpd.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpVersion;
import org.apache.http.RequestLine;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.core.RequestParameters;
import org.tamacat.httpd.mock.HttpObjectFactory;

public class RequestUtilsTest {

	private HttpContext context;

	@Before
	public void setUp() throws Exception {
		context = HttpObjectFactory.createHttpContext();
		InetAddress address = InetAddress.getByName("127.0.0.1");
		context.setAttribute(RequestUtils.REMOTE_ADDRESS, address);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRequestUtils() {
		new RequestUtils();
	}

	@Test
	public void testRequestLine() throws Exception {
		RequestLine line = new BasicRequestLine("GET", "http://localhost/", HttpVersion.HTTP_1_1);
		assertEquals("/", RequestUtils.getRequestLine(line).getUri());

		RequestLine line2 = new BasicRequestLine("GET", "/", HttpVersion.HTTP_1_1);
		assertEquals("/", RequestUtils.getRequestLine(line2).getUri());
		assertSame(line2, RequestUtils.getRequestLine(line2));
	}

	@Test
	public void testRequestPathString() throws Exception {
		assertEquals("/", RequestUtils.getRequestPath("http://localhost/"));
		assertEquals("/test", RequestUtils.getRequestPath("http://localhost/test"));
		assertEquals("/test/", RequestUtils.getRequestPath("http://localhost/test/"));
		assertEquals("/test?test=test", RequestUtils.getRequestPath("http://localhost/test?test=test"));
		assertEquals("/", RequestUtils.getRequestPath("https://localhost/"));

		assertEquals("/", RequestUtils.getRequestPath("http://localhost:8080/"));
		assertEquals("/test/", RequestUtils.getRequestPath("http://localhost:8080/test/"));
		assertEquals("http://localhost", RequestUtils.getRequestPath("http://localhost"));
		assertEquals("ttp://localhost/", RequestUtils.getRequestPath("ttp://localhost/"));
		assertEquals("http//localhost/", RequestUtils.getRequestPath("http//localhost/"));

		assertEquals("/", RequestUtils.getRequestPath("/"));
		assertEquals("/test", RequestUtils.getRequestPath("/test"));
		assertEquals("/test/", RequestUtils.getRequestPath("/test/"));
		assertEquals("", RequestUtils.getRequestPath(""));
	}

	@Test
	public void testSetParameters() throws Exception {
		HttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest("POST", "/test.html");
		request.setEntity(new StringEntity("<html></html>"));
		RequestUtils.setParameters(request, context, "UTF-8");
	}

	@Test
	public void testSetParametersHttpContextRequestParameters() throws Exception {
		RequestParameters params = new RequestParameters();
		params.setParameter("key1", "value1");
		RequestUtils.setParameters(context, params);
	}

	@Test
	public void testGetRequestPath() {
		assertEquals("/test.html", RequestUtils.getRequestPath(new BasicHttpRequest("GET", "/test.html")));
		assertEquals("/test.html", RequestUtils.getRequestPath(new BasicHttpRequest("GET", "/test.html?id=test")));
	}

	@Test
	public void testGetRemoteIPAddress() {
		String ipaddress = RequestUtils.getRemoteIPAddress(context);
		assertEquals("127.0.0.1", ipaddress);

		HttpContext ctx = new BasicHttpContext();
		assertEquals("", RequestUtils.getRemoteIPAddress(ctx));
	}

	@Test
	public void testGetRequestHost() throws Exception {
		HttpRequest request = new BasicHttpRequest("GET", "/test.html");

		URL url = RequestUtils.getRequestURL(request, null);
		assertNull(url);

		request.setHeader(HTTP.TARGET_HOST, "example.com");
		url = RequestUtils.getRequestURL(request, null);
		assertEquals("http://example.com/test.html", url.toString());

		ServerConfig serverConfig = new ServerConfig();
		serverConfig.setParam("Port", "8080");
		ServiceUrl serviceUrl = new ServiceUrl(serverConfig);
		url = RequestUtils.getRequestURL(request, null, serviceUrl);
		assertEquals("http://example.com:8080/test.html", url.toString());

		serverConfig = new ServerConfig();
		serverConfig.setParam("Port", "443");
		serverConfig.setParam("https", "true");
		serviceUrl = new ServiceUrl(serverConfig);
		url = RequestUtils.getRequestURL(request, null, serviceUrl);
		assertEquals("https://example.com/test.html", url.toString());
	}

	@Test
	public void testGetRequestHostURL() {
		HttpRequest request = new BasicHttpRequest("GET", "/test.html");

		String url = RequestUtils.getRequestHostURL(request, null, null);
		assertNull(url);

		request.setHeader(HTTP.TARGET_HOST, "example.com");
		url = RequestUtils.getRequestHostURL(request, null, null);
		assertEquals("http://example.com", url);

		ServerConfig serverConfig = new ServerConfig();
		serverConfig.setParam("Port", "8080");
		ServiceUrl serviceUrl = new ServiceUrl(serverConfig);
		url = RequestUtils.getRequestHostURL(request, null, serviceUrl);
		assertEquals("http://example.com:8080", url.toString());

		serverConfig = new ServerConfig();
		serverConfig.setParam("Port", "443");
		serverConfig.setParam("https", "true");
		serviceUrl = new ServiceUrl(serverConfig);
		url = RequestUtils.getRequestHostURL(request, null, serviceUrl);
		assertEquals("https://example.com", url.toString());
	}

	@Test
	public void testGetRequestHostURLHttpRequestHttpContext() {
		HttpRequest request = new BasicHttpRequest("GET", "/test.html");

		String url = RequestUtils.getRequestHostURL(request, context);
		assertNull(url);

		request.setHeader(HTTP.TARGET_HOST, "example.com");
		url = RequestUtils.getRequestHostURL(request, context);
		assertEquals("http://example.com", url);

		request.setHeader(HTTP.TARGET_HOST, "example.com:8080");
		url = RequestUtils.getRequestHostURL(request, context);
		assertEquals("http://example.com:8080", url);

		request.setHeader(HTTP.TARGET_HOST, "example.com:80");
		url = RequestUtils.getRequestHostURL(request, context);
		assertEquals("http://example.com", url);
	}

	@Test
	public void testGetInputStream() throws IOException {
		HttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest("POST", "/test.html");
		request.setEntity(new StringEntity("<html></html>"));
		assertNotNull(RequestUtils.getInputStream(request));

		HttpEntityEnclosingRequest request2 = new BasicHttpEntityEnclosingRequest("POST", "/test.html");
		assertNull(RequestUtils.getInputStream(request2));

		HttpRequest request3 = new BasicHttpRequest("POST", "/test.html");
		assertNull(RequestUtils.getInputStream(request3));
	}

	@Test
	public void testIsMultipart() {
		Header header = new BasicHeader(HTTP.CONTENT_TYPE, "multipart/form-data");
		HttpRequest request = new BasicHttpRequest("POST", "/test.html");
		request.setHeader(header);

		assertTrue(RequestUtils.isMultipart(request));

		HttpRequest request2 = new BasicHttpRequest("GET", "/test.html");
		assertFalse(RequestUtils.isMultipart(request2));
	}

	@Test
	public void testDecode() {
		assertEquals("", RequestUtils.decode("", "UTF-8"));
		assertEquals("abc def", RequestUtils.decode("abc%20def", "UTF-8"));
	}

	@Test
	public void testGetPathPrefix() {
		HttpRequest request = new BasicHttpRequest("GET", "/test.html");
		assertEquals("/", RequestUtils.getPathPrefix(request));

		request = new BasicHttpRequest("GET", "/test/index.html");
		assertEquals("/test/", RequestUtils.getPathPrefix(request));

		request = new BasicHttpRequest("GET", "/test/aaaa/index.html");
		assertEquals("/test/aaaa/", RequestUtils.getPathPrefix(request));
	}
}
