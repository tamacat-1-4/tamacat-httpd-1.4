/*
 * Copyright (c) 2010, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.config;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.tamacat.httpd.config.ServiceType;
import org.tamacat.httpd.config.lb.LbHealthCheckServiceUrl;
import org.tamacat.httpd.config.lb.LbServiceUrlFactory;
import org.tamacat.util.IOUtils;
import org.tamacat.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>XML parser for VirtualHostConfig.<br>
 * 
 * <p>The XML file is key of "url-config.file" in server.properties.<br>
 * 
 * <p><b>Sample Usage:</b>
 * <pre>{@code
 * <?xml version="1.0" encoding="UTF-8"?> 
 * <service-config>
 *   <service host="http://localhost">
 *     <url path="/test/" type="reverse" handler="ReverseHandler">
 *       <reverse>http://localhost:8080/test/</reverse>
 *     </url>
 *   </service>
 * </service-config>
 * }
 * </pre>
 */
public class ServiceConfigParser {

	static final String REVERSE_CONFIG = "service-config";
	static final String SERVICE = "service";
	static final String HOST = "host";
	static final String URL = "url";
	static final String PATH = "path";
	static final String TYPE = "type";
	static final String REVERSE = "reverse";
	static final String HANDLER = "handler";
	static final String LB_METHOD = "lb-method";
	
	static final String URL_CONFIG = "url-config.xml";
	protected ServerConfig serverConfig;
	
	protected HostServiceConfig config = new HostServiceConfig();

	/**
	 * <p>Constructs with the specified {@link ServerConfig}.
	 * @param serverConfig
	 */
	public ServiceConfigParser(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}
	
	/**
	 * <p>Returns the {@link ServiceConfig}.
	 * @return mapping to ServiceConfig.
	 */
	public HostServiceConfig getConfig() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			String xml = serverConfig.getParam("url-config.file", URL_CONFIG);
			Document doc = builder.parse(IOUtils.getInputStream(xml, getClass().getClassLoader()));
			return parse(doc);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	HostServiceConfig parse(Document doc) {
		Element root = doc.getDocumentElement();
		NodeList services = root.getChildNodes();
		parseServices(services);
		return config;
	}
	
	//<service>xxx</service>
	void parseServices(NodeList services) {
		for (int i=0; i<services.getLength(); i++) {
			Node node = services.item(i);
			if (SERVICE.equals(node.getNodeName())) {
				parseServiceNode(node);
			}
		}
	}

	//<url path="/xxx/">
	//  <reverse>xxx</reverse>
	void parseServiceNode(Node service) {
		//<service host="xxx">
		String host = getHost(service);
		ServiceConfig serviceConfig = new ServiceConfig();
		//<url>xxx</url>
		NodeList urlNodes = service.getChildNodes();
		for (int i=0; i<urlNodes.getLength(); i++) {
			Node urlNode = urlNodes.item(i);
			ServiceUrl serviceUrl = getServiceUrl(urlNode, host);
			//<url path="xxx">
			if (URL.equals(urlNode.getNodeName())) {
				//<reverse>xxx</reverse>
				if (serviceUrl.isType(ServiceType.REVERSE)) {
					serviceUrl = getReverseUrl(serviceUrl, urlNode);
				} else
				//Load Balancer
				if (serviceUrl.isType(ServiceType.LB)) {
					serviceUrl = getLbServiceUrl(serviceUrl, urlNode, host);
				}
				serviceConfig.addServiceUrl(serviceUrl);
			}
		}
		if (host == null || "default".equalsIgnoreCase(host)) {
			config.setDefaultServiceConfig(serviceConfig);
		} else {
			config.setServiceConfig(host, serviceConfig);
		}
	}
	
	String getHost(Node service) {
		NamedNodeMap attr = service.getAttributes();
		if (attr != null) {
			Node hostNode = attr.getNamedItem(HOST);
			if (hostNode != null) {
				return hostNode.getNodeValue();
			}
		}
		return null;
	}
	
	ServiceUrl getServiceUrl(Node urlNode, String host) {
		ServiceUrl serviceUrl = new ServiceUrl(serverConfig);
		NamedNodeMap urlAttrs = urlNode.getAttributes();
		if (urlAttrs != null) {
			Node path = urlAttrs.getNamedItem(PATH);
			if (StringUtils.isNotEmpty(path)) {
				serviceUrl.setPath(path.getNodeValue());
			}
			Node type = urlAttrs.getNamedItem(TYPE);
			if (StringUtils.isNotEmpty(type)) {
				serviceUrl.setType(ServiceType.find(type.getNodeValue()));
				if (serviceUrl.isType(ServiceType.LB)) {
					Node lbMethod = urlAttrs.getNamedItem(LB_METHOD);
					if (StringUtils.isNotEmpty(lbMethod)) {
						serviceUrl.setLoadBalancerMethod(lbMethod.getNodeValue());
					}
				}
			}
			Node handler = urlAttrs.getNamedItem(HANDLER);
			if (StringUtils.isNotEmpty(handler)) {					
				serviceUrl.setHandlerName(handler.getNodeValue());
			}
			serviceUrl.setHost(getURL(host));
		}
		return serviceUrl;
	}
	
	ServiceUrl getReverseUrl(ServiceUrl serviceUrl, Node urlNode) {
		ReverseUrl reverseUrl = new DefaultReverseUrl(serviceUrl);
		NodeList reverseNodes = urlNode.getChildNodes();
		for (int i=0; i<reverseNodes.getLength(); i++) {
			Node reverseNode = reverseNodes.item(i);
			if (REVERSE.equals(reverseNode.getNodeName())) {
				String reverse = reverseNode.getTextContent();
				reverseUrl.setReverse(getURL(reverse));
				break;
			}
		}
		serviceUrl.setReverseUrl(reverseUrl);
		return serviceUrl;
	}
	
	ServiceUrl getLbServiceUrl(ServiceUrl serviceUrl, Node urlNode, String host) {
		LbHealthCheckServiceUrl lbServiceUrl = LbServiceUrlFactory.getServiceUrl(serviceUrl);
		lbServiceUrl.setPath(serviceUrl.getPath());
		lbServiceUrl.setHandlerName(serviceUrl.getHandlerName());
		lbServiceUrl.setType(serviceUrl.getType());
		lbServiceUrl.setHost(getURL(host));
		NodeList reverseNodes = urlNode.getChildNodes();
		for (int i=0; i<reverseNodes.getLength(); i++) {
			ReverseUrl reverseUrl = new DefaultReverseUrl(lbServiceUrl);
			Node reverseNode = reverseNodes.item(i);
			if (REVERSE.equals(reverseNode.getNodeName())) {
				String reverse = reverseNode.getTextContent();
				reverseUrl.setReverse(getURL(reverse));
				lbServiceUrl.setReverseUrl(reverseUrl);
			}
		}
		lbServiceUrl.startHealthCheck();
		return lbServiceUrl;
	}
	
	protected URL getURL(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			return null;
		}
	}
}
