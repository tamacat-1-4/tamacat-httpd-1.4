/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config;

import java.util.ArrayList;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tamacat.httpd.config.ServiceType;

/**
 * <p>Service configuration.
 */
public class ServiceConfig {

	private List<ServiceUrl> reverseUrls = new ArrayList<>();
	private Map<String,ServiceUrl> regulars = new HashMap<>();
	private Map<String,ServiceUrl> reverses = new HashMap<>();
	
	/**
	 * <p>The method which acquires list of {@link ServiceUrl}.
	 * @return {@literal List<ServiceUrl>}
	 */
	public List<ServiceUrl> getServiceUrlList() {
		return reverseUrls;
	}
	
	/**
	 * <p>The method which acquires {@link ServiceUrl}.
	 * @param path
	 * @return {@literal ServiceUrl}
	 */
	public ServiceUrl getServiceUrl(String path) {
		return regulars.get(path);
	}

	/**
	 * <p>The method which registers ServiceUrl.
	 * @param serviceUrl {@link ServiceUrl}
	 */
	public void addServiceUrl(ServiceUrl serviceUrl) {
		regulars.put(serviceUrl.getPath(), serviceUrl);
		if (serviceUrl.isType(ServiceType.REVERSE)) {
			reverses.put(serviceUrl.getReverseUrl().getReverse().getPath(), serviceUrl);
		}
		reverseUrls.add(serviceUrl);
	}
	
	/**
	 * <p>The method which unregisters ServiceUrl.
	 * @param serviceUrl
	 */
	public void removeServiceUrl(ServiceUrl serviceUrl) {
		regulars.remove(serviceUrl.getPath());
		if (serviceUrl.isType(ServiceType.REVERSE)) {
			reverses.remove(serviceUrl.getReverseUrl().getReverse().getPath());
		}
		reverseUrls.remove(serviceUrl);
	}
}
