package org.tamacat.httpd.filter;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.util.Args;

/**
 * Support a keep-alive for Transfer-Encoding chunked on HTTP/1.1.
 * (customize the org.apache.http.protocol.ResponseConnControl)
 */
public class HttpResponseConnControl extends ResponseConnControl {

	@Override
	public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
		Args.notNull(response, "HTTP response");

		final HttpCoreContext corecontext = HttpCoreContext.adapt(context);

		// Always drop connection after certain type of responses
		final int status = response.getStatusLine().getStatusCode();
		if (status == HttpStatus.SC_BAD_REQUEST || status == HttpStatus.SC_REQUEST_TIMEOUT
				|| status == HttpStatus.SC_LENGTH_REQUIRED || status == HttpStatus.SC_REQUEST_TOO_LONG
				|| status == HttpStatus.SC_REQUEST_URI_TOO_LONG || status == HttpStatus.SC_SERVICE_UNAVAILABLE
				|| status == HttpStatus.SC_NOT_IMPLEMENTED) {
			response.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
			return;
		}
		final Header explicit = response.getFirstHeader(HTTP.CONN_DIRECTIVE);
		if (explicit != null && HTTP.CONN_CLOSE.equalsIgnoreCase(explicit.getValue())) {
			// Connection persistence explicitly disabled
			return;
		}
		// Always drop connection for HTTP/1.0 responses and below
		// if the content body cannot be correctly delimited
		final HttpEntity entity = response.getEntity();
		if (entity != null) {
			final ProtocolVersion ver = response.getStatusLine().getProtocolVersion();
			if (ver.greaterEquals(HttpVersion.HTTP_1_1)) {
				response.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
				return;
			} else if (entity.getContentLength() < 0 && (!entity.isChunked() || ver.lessEquals(HttpVersion.HTTP_1_0))) {
				response.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
				return;
			}
		}
		// Drop connection if requested by the client or request was <= 1.0
		final HttpRequest request = corecontext.getRequest();
		if (request != null) {
			final Header header = request.getFirstHeader(HTTP.CONN_DIRECTIVE);
			if (header != null) {
				response.setHeader(HTTP.CONN_DIRECTIVE, header.getValue());
			} else if (request.getProtocolVersion().lessEquals(HttpVersion.HTTP_1_0)) {
				response.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
			}
		}
	}
}
