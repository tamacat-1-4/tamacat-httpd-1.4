package org.tamacat.httpd.mock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;

public class DummySocket extends Socket {

	boolean created;
	boolean bound;
	boolean closed;
	boolean connected;
	String data = "test";

	public DummySocket() {

	}

	void accept() {
		connected = true;
		created = true;
		bound = true;
	}

	@Override
	public void connect(SocketAddress endpoint, int timeout) throws IOException {
		accept();
	}

	@Override
	public void bind(SocketAddress bindpoint) throws IOException {

	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	public void close() throws IOException {
		closed = true;
	}

	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(data.getBytes());
	}

	public OutputStream getOutputStream() throws IOException {
		return new ByteArrayOutputStream();
	}
}
