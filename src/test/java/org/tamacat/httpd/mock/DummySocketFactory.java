/*
 * Copyright (c) 2014, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.mock;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

public class DummySocketFactory extends SocketFactory {

	public DummySocketFactory() {
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException,
			UnknownHostException {
		return new DummySocket();
	}

	@Override
	public Socket createSocket(InetAddress host, int port) throws IOException {
		return new DummySocket();
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localHost,
			int localPort) throws IOException, UnknownHostException {
		return new DummySocket();
	}

	@Override
	public Socket createSocket(InetAddress address, int port,
			InetAddress localAddress, int localPort) throws IOException {
		return new DummySocket();
	}

}
