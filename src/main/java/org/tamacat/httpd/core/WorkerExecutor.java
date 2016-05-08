/*
 * Copyright (c) 2013, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core;

import java.io.IOException;
import java.net.Socket;

import org.apache.http.protocol.HttpService;
import org.tamacat.httpd.config.ServerConfig;

public interface WorkerExecutor {

	void setServerConfig(ServerConfig serverConfig);

	void setHttpService(HttpService httpService);

	void execute(Socket socket) throws IOException;

	void shutdown();
}
