/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.tamacat.httpd.util.HeaderUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.ExceptionUtils;
import org.tamacat.util.StringUtils;

/**
 * <p>Server-side interceptor to handle Gzip-encoded responses.<br>
 * The cord of the basis is Apache HttpComponents {@code ResponseGzipCompress.java}.</p>
 *
 * <pre>Example:{@code components.xml}
 * {@code <bean id="gzip" class="org.tamacat.httpd.filter.GzipResponseInterceptor">
 *  <property name="contentType">
 *    <value>html,xml,css,javascript</value>
 *  </property>
 * </bean>
 * }</pre>
 *
 * {@link http://svn.apache.org/repos/asf/httpcomponents/httpcore/trunk/contrib/src/main/java/org/apache/http/contrib/compress/ResponseGzipCompress.java}
 */
public class GzipResponseInterceptor implements HttpResponseInterceptor {

	static final Log LOG = LogFactory.getLog(GzipResponseInterceptor.class);
	protected static final String ACCEPT_ENCODING = "Accept-Encoding";
	protected static final String GZIP_CODEC = "gzip";

	protected Set<String> contentTypes = new HashSet<String>();
	protected boolean useAll = true;

	@Override
	public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
		if (context == null) {
			throw new IllegalArgumentException("HTTP context may not be null");
		}
		HttpRequest request = (HttpRequest) context.getAttribute(HttpCoreContext.HTTP_REQUEST);
		Header aeheader = request != null ? request.getFirstHeader(ACCEPT_ENCODING) : null;
		if (request != null && request.getProtocolVersion().greaterEquals(HttpVersion.HTTP_1_1)
				&& aeheader != null && useCompress(response.getFirstHeader(HTTP.CONTENT_TYPE))) {
			String ua = HeaderUtils.getHeader(request, "User-Agent");
			if (ua != null && ua.indexOf("MSIE 6.0") >= 0) {
				return; //Skipped for IE6 bug(KB823386)
			}
			HeaderElement[] codecs = aeheader.getElements();
			for (int i=0; i<codecs.length; i++) {
				if (codecs[i].getName().equalsIgnoreCase(GZIP_CODEC)) {
					GzipCompressingEntity entity = new GzipCompressingEntity(response.getEntity());
					response.setEntity(entity);
					response.setHeader(entity.getContentEncoding()); //Content-Encoding:gzip
					response.setHeader(HTTP.TRANSFER_ENCODING, HTTP.CHUNK_CODING); //Transfer-Encoding:chunked
					response.removeHeaders(HTTP.CONTENT_LEN);
					return;
				}
			}
		}
	}

	/**
	 * <p>Set the content type of the gzip compression.<br>
	 * default are all content types to compressed.</p>
	 * <p>The {@code contentType} value is case insensitive,<br>
	 * and the white space of before and after is trimmed.</p>
	 *
	 * <p>Examples: {@code contentType="html, css, javascript, xml" }
	 * <ul>
	 *   <li>text/html</li>
	 *   <li>text/css</li>
	 *   <li>text/javascript</li>
	 *   <li>application/xml</li>
	 *   <li>text/xml</li>
	 * </ul>
	 * @param contentType Comma Separated Value of content-type or sub types.
	 */
	public void setContentType(String contentType) {
		if (StringUtils.isNotEmpty(contentType)) {
			String[] csv = StringUtils.split(contentType, ",");
			for (String t : csv) {
				contentTypes.add(t.toLowerCase());
				useAll = false;
				String[] types = t.split(";")[0].split("/");
				if (types.length >= 2) {
					contentTypes.add(types[1].toLowerCase());
				}
			}
		}
	}

	/**
	 * <p>Check for use compress contents.
	 * @param contentType
	 * @return true use compress.
	 */
	boolean useCompress(Header contentType) {
		if (contentType == null) return false;
		String type = contentType.getValue();
		if (useAll || contentTypes.contains(type)) {
			return true;
		} else {
			//Get the content sub type. (text/html; charset=UTF-8 -> html)
			String[] types = type != null ? type.split(";")[0].split("/") : new String[0];
			if (types.length >= 2 && contentTypes.contains(types[1])) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * <p>Wrapping entity that compresses content when {@link #writeTo writing}.
	 * {@link http://svn.apache.org/repos/asf/httpcomponents/httpcore/trunk/contrib/src/main/java/org/apache/http/contrib/compress/GzipCompressingEntity.java}
	 */
	static class GzipCompressingEntity extends HttpEntityWrapper {
		public GzipCompressingEntity(HttpEntity entity) {
			super(entity);
		}

		@Override
		public Header getContentEncoding() {
			return new BasicHeader(HTTP.CONTENT_ENCODING, GZIP_CODEC);
		}

		@Override
		public long getContentLength() {
			return -1;
		}

		@Override
		public boolean isChunked() {
			// force content chunking
			return true;
		}

		@Override
		public void writeTo(OutputStream outstream) throws IOException {
			if (outstream == null) {
				throw new IllegalArgumentException("Output stream may not be null");
			}
			GZIPOutputStream gzip = new GZIPOutputStream(outstream);
			try {
				wrappedEntity.writeTo(gzip);
			} finally {
				try {
					gzip.close();
				} catch (IOException e) {
					LOG.debug(ExceptionUtils.getStackTrace(e, 100));
				}
			}
		}
	}
}
