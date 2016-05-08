package org.tamacat.httpd.core;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.config.MessageConstraints;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;
import org.tamacat.httpd.config.ServerConfig;

/**
 * Get the backend server configuration parameters from the
 *
 * server.properties.
 *  default value is:
 *  - BackEndSocketBufferSize=8192
 *  - BackEndSocketTimeout=5000
 */
public class ClientHttpConnection extends DefaultBHttpClientConnection {

	long connStartTime = System.currentTimeMillis();
	long lastAccessTime = System.currentTimeMillis();
	
	public ClientHttpConnection(ServerConfig serverConfig) {
		super(serverConfig.getParam("BackEndSocketBufferSize", 8192));
		setSocketTimeout(serverConfig.getParam("BackEndSocketTimeout", 5000));
	}
	
	public ClientHttpConnection(int buffersize) {
		super(buffersize);
	}

	public ClientHttpConnection(int buffersize, CharsetDecoder chardecoder, CharsetEncoder charencoder,
			MessageConstraints constraints) {
		super(buffersize, chardecoder, charencoder, constraints);
	}

	public ClientHttpConnection(int buffersize, int fragmentSizeHint, CharsetDecoder chardecoder,
			CharsetEncoder charencoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy,
			ContentLengthStrategy outgoingContentStrategy, HttpMessageWriterFactory<HttpRequest> requestWriterFactory,
			HttpMessageParserFactory<HttpResponse> responseParserFactory) {
		super(buffersize, fragmentSizeHint, chardecoder, charencoder, constraints, incomingContentStrategy,
				outgoingContentStrategy, requestWriterFactory, responseParserFactory);
	}
	
	@Override
	public void bind(final Socket socket) throws IOException {
		connStartTime = System.currentTimeMillis();
		lastAccessTime = connStartTime;
		super.bind(socket);
	}
	
	public long getConnectionStartTime() {
		return connStartTime;
	}
	
	public long getLastAccessTime() {
		long last = lastAccessTime;
		lastAccessTime = System.currentTimeMillis();
		return last;
	}
}
