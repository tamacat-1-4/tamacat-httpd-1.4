/*
 * Copyright (c) 2014, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core.jmx;

import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.core.HttpEngine;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.ExceptionUtils;
import org.tamacat.util.StringUtils;

public class JMXServer {

	static final Log LOG = LogFactory.getLog(JMXServer.class);

	private ServerConfig serverConfig;
	private JMXConnectorServer jmxServer;
	private Registry rmiRegistry;
	private boolean isMXServerStarted;
	private ObjectName objectName;
	HttpEngine engine;

	public JMXServer(HttpEngine engine) {
		this.engine = engine;
	}

	public boolean isMXServerStarted() {
		return isMXServerStarted;
	}

	public void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}

	//install
	//http://ws-jmx-connector.dev.java.net/files/documents/4956/114781/jsr262-ri.jar
	//https://jax-ws.dev.java.net/2.1.1/JAXWS2.1.1_20070501.jar
	public void registerMXServer() {
		try {
			//"service:jmx:rmi:///jndi/rmi://localhost/httpd";
			//"ws", "localhost", 9999, "/admin"
			String jmxUrl = serverConfig.getParam("JMX.server-url");
			if (!isMXServerStarted && StringUtils.isNotEmpty(jmxUrl)) {
				String name = serverConfig.getParam(
						"JMX.objectname","org.tamacat.httpd:type=HttpEngine");

				int rmiPort = serverConfig.getParam("JMX.rmi.port", -1);
				if (rmiPort > 0) {
					rmiRegistry = LocateRegistry.createRegistry(rmiPort);
				}
				MBeanServer server = ManagementFactory.getPlatformMBeanServer();
				objectName = new ObjectName(name);
				server.registerMBean(engine, objectName);

				HashMap<String,Object> env = new HashMap<>();
				//env.put("jmx.remote.x.password.file", serverConfig.getParam("JMX.password", ""));
				//env.put("jmx.remote.x.access.file", serverConfig.getParam("JMX.access", ""));
				jmxServer = JMXConnectorServerFactory.newJMXConnectorServer(
					new JMXServiceURL(jmxUrl), env, server);
				jmxServer.start();
				isMXServerStarted = true;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
			LOG.debug(ExceptionUtils.getStackTrace(e));
		}
	}

	public void unregisterMXServer() {
		if (isMXServerStarted) {
			MBeanServer server = ManagementFactory.getPlatformMBeanServer();
			try {
				server.unregisterMBean(objectName);
				if (jmxServer != null) jmxServer.stop();
				if (rmiRegistry != null) UnicastRemoteObject.unexportObject(rmiRegistry, true);
				isMXServerStarted = false;
			} catch (Exception e) {
				LOG.error(e.getMessage());
				LOG.warn(ExceptionUtils.getStackTrace(e));
			}
		}
	}
}
