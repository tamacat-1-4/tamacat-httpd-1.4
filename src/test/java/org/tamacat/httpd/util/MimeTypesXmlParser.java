/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.util;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.tamacat.util.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>This is a tool that extracts mimetypes
 *  from web.xml of Apache Tomcat. 
 */
public class MimeTypesXmlParser {

	public static void main(String[] args) {
		new MimeTypesXmlParser().getReverseConfig();
	}
	
	static final String WEBAPP = "web-app";
	static final String MIME_MAPPING = "mime-mapping";
	static final String EXTENSION = "extension";
	static final String MIME_TYPE = "mime-type";
	
	LinkedHashMap<String,String> mimetypes = new LinkedHashMap<String,String>();
	
	public MimeTypesXmlParser() {
	}
	
	public void getReverseConfig() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(IOUtils.getInputStream("web.xml"));
			parse(doc);
			for (Entry<String, String> entry : mimetypes.entrySet()) {
				System.out.println(entry.getKey() +"=" + entry.getValue());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	void parse(Document doc) {
		Element root = doc.getDocumentElement();
		NodeList webapp = root.getChildNodes();
		parseMimeMappingNode(webapp);
	}
	
	//<mime-mapping>
	//  <extension>
	//  <mime-type>xxx</mime-type>
	void parseMimeMappingNode(NodeList mappingNodes) {
		for (int i=0; i<mappingNodes.getLength(); i++) {
			Node mappingNode = mappingNodes.item(i);
			if (MIME_MAPPING.equals(mappingNode.getNodeName())) {
				NodeList mappingChildNodes = mappingNode.getChildNodes();
				String ext = null;
				String type = null;
				for (int j=0; j<mappingChildNodes.getLength(); j++) {
					Node n = mappingChildNodes.item(j);
					if (EXTENSION.equals(n.getNodeName())) {
						ext = n.getTextContent();
					}
					if (MIME_TYPE.equals(n.getNodeName())) {
						type = n.getTextContent();
					}
					if (ext != null && type != null) break;
				}
				mimetypes.put(ext, type);				
			}
		}
	}
}
