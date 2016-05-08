/*
 * Copyright (c) 2010, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config;

import java.util.HashMap;
import java.util.Set;

/**
 * <p>It is the service configuration for Virtual host.
 * The mapping of hostname and {@link ServiceConfig}.
 */
public class HostServiceConfig {

	private HashMap<String,ServiceConfig> serviceConfigs = new HashMap<>();
	
	public void setDefaultServiceConfig(ServiceConfig defaultConfig) {
		setServiceConfig("default", defaultConfig);
	}
	
	public void setServiceConfig(String host, ServiceConfig config) {
		serviceConfigs.put(host, config);
	}
	
	public ServiceConfig getServiceConfig(String host) {
		return serviceConfigs.get(host);
	}
	
	public ServiceConfig getDefaultServiceConfig() {
		return getServiceConfig("default");
	}
	
	public Set<String> getHosts() {
		return serviceConfigs.keySet();
	}
}
