/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.core.jmx;

import java.io.IOException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JMXClient {

	private JMXConnector connector;
	private MBeanServerConnection conn;
	private boolean isAutoConnect;
	
    //"service:jmx:rmi:///jndi/rmi://localhost/hello"; 
	public JMXClient(String serviceUrl) {
		try {
			JMXServiceURL url = new JMXServiceURL(serviceUrl);
			connector = JMXConnectorFactory.connect(url);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public JMXClient(String protocol, String host, int port, String path) {
		try {
			JMXServiceURL url = new JMXServiceURL(protocol, host, port, path);
			connector = JMXConnectorFactory.connect(url);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(ObjectName name, String operationName, T defaultValue) {
		try {
			if (isAutoConnect) {
				this.conn = connect();
			}
			T result = (T) conn.invoke(name, operationName, null, null);
			return result != null ? result : defaultValue;
		} catch (Exception e) {
			return defaultValue;
		} finally {
			if (isAutoConnect) {
				close();
			}
		}
	}

	public MBeanServerConnection connect() throws IOException {
		conn = connector.getMBeanServerConnection();
		if (conn != null) isAutoConnect = true;
		return conn;
	}
	
	public void close() {
		try {
			if (connector != null) connector.close();
		} catch (IOException e) {
		} finally {
			isAutoConnect = false;
		}
	}
}
