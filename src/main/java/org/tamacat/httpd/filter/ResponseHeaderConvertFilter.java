/*
 * Copyright (c) 2019 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.StringUtils;

/**
 * ResponseFilter for Response Header convert.
 * <li>headerNames: Header1,Header2,Header3,...
 * <li>convertValues: BeforeValue=AfterValue
 *
 * ex) conf/components.xml
 * ----------------------------------
 * <bean id="ResponseHeaderFilter" class="org.tamacat.httpd.filter.ResponseHeaderConvertFilter">
 *   <property name="headerNames">
 *     <value>Location</value>
 *   </property>
 *   <property name="convertValues">
 *     <value>http://localhost:8080/=https://test.example.com/</value>
 *   </property>
 * </bean>
 * 
 * Before: Location: http://localhost:8080/test.html
 * After : Location: https://test.example.com/test.html
 */
public class ResponseHeaderConvertFilter implements ResponseFilter {

	static final Log LOG = LogFactory.getLog(ResponseHeaderConvertFilter.class);
		
	protected ServiceUrl serviceUrl;
	protected Set<String> headerNames = new LinkedHashSet<>();
	
	protected Map<String, String> convert = new LinkedHashMap<>();
	
	public void setHeaderNames(String names) {
		String[] values = StringUtils.split(names, ",");
		if (values.length > 0) {
			this.headerNames.addAll(Arrays.asList(values));
		}
	}
	
	public void setConvertValues(String values) {
		String[] conv = StringUtils.split(values, "=");
		if (conv.length == 2) { 
			this.convert.put(conv[0], conv[1]);
		}
	}
	
	@Override
	public void init(ServiceUrl serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	@Override
	public void afterResponse(HttpRequest req, HttpResponse resp, HttpContext context) {
		for (String headerName : headerNames) {
			Header[] headers = resp.getHeaders(headerName);
			for (Header header : headers) {
				String value = header.getValue();
				if (StringUtils.isNotEmpty(value)) {
					String convertedValue = convertHeaderValue(value);
					LOG.trace("[Filter] "+value +" => "+convertedValue);
					resp.removeHeader(header);
					resp.addHeader(new BasicHeader(headerName, convertedValue));
				}
			}
		}
	}

	protected String convertHeaderValue(String value) {
		Set<String> beforeValues = convert.keySet();
		for (String before : beforeValues) {
			if (value.indexOf(before) >= 0) {
				return value.replace(before, convert.get(before));
			}
		}
		return value;
	}
}
