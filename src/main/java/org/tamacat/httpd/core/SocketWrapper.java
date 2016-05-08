/*
 * Copyright (c) 2010, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.net.Socket;

public class SocketWrapper {

	private Socket socket;
	private boolean isWebSocketSupport;
	private boolean isWebDAVSupport;
	
	public boolean isWebDAVSupport() {
		return isWebDAVSupport;
	}

	public void setWebDAVSupport(boolean isWebDAVSupport) {
		this.isWebDAVSupport = isWebDAVSupport;
	}

	public SocketWrapper(Socket socket) {
		this.socket = socket;
	}
	
	public void setWebSocketSupport(boolean isWebSocketSupport) {
		this.isWebSocketSupport = isWebSocketSupport;
	}
	
	public boolean isWebSocketSupport() {
		return isWebSocketSupport;
	}
	
	public Socket getSocket() {
		return socket;
	}
}
