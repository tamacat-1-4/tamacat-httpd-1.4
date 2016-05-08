/*
 * Copyright (c) 2010 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.ForbiddenException;
import org.tamacat.httpd.util.IpAddressMatcher;
import org.tamacat.httpd.util.RequestUtils;

public class ClientIPAccessControlFilter implements RequestFilter {
	private List<IpAddressMatcher> allowMatchers = new ArrayList<>();
	private List<IpAddressMatcher> denyMatchers = new ArrayList<>();

	protected ServiceUrl serviceUrl;

	protected boolean useForwardHeader;
	protected String forwardHeader = "X-Forwarded-For";
	
	public void setUseForwardHeader(boolean forwardHeader) {
		this.useForwardHeader = forwardHeader;
	}

	public void setForwardHeader(String forwardHeader) {
		this.forwardHeader = forwardHeader;
	}
	
	@Override
	public void doFilter(HttpRequest request, HttpResponse response,
			HttpContext context) {
		String client = RequestUtils.getRemoteIPAddress(request, context, useForwardHeader, forwardHeader);
		boolean isAllow = false;
		for (IpAddressMatcher allow : allowMatchers) {
			if ("0.0.0.0/0".equals(allow.getIpAddress()) || allow.matches(client)) {
				isAllow = true;
				break;
			}
		}
		if (isAllow == false) {
			//allows only -> denied all.
			if (denyMatchers.size() == 0) {
				throw new ForbiddenException();
			}
			for (IpAddressMatcher deny : denyMatchers) {
				if ("0.0.0.0/0".equals(deny.getIpAddress()) || deny.matches(client)) {
					throw new ForbiddenException();
				}
			}
		}
	}

	@Override
	public void init(ServiceUrl serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public void setAllow(String address) {
		setPattern(address, true);
	}
	
	public void setDeny(String address) {
		setPattern(address, false);
	}

	private void setPattern(String address, boolean isAllow) {
		if (address.indexOf(".*") >= 0 && address.indexOf('/')==-1) {
			String[] ip = address.split("\\.");
			StringBuilder pattern = new StringBuilder();
			int num = 0;
			for (int i=0; i<4; i++) {
				if (pattern.length() > 0) {
					pattern.append(".");
				}
				if (ip.length > i && "*".equals(ip[i])==false) {
					pattern.append(ip[i]);
				} else {
					pattern.append("0");
					num++;
				}
			}
			if (num >= 1) {
				pattern.append("/"+(32-(8*num)));
			}
			address = pattern.toString();
		} else if ("*".equals(address)) {
			address = "0.0.0.0/0";
		}
		IpAddressMatcher matcher = new IpAddressMatcher(address);
		if (isAllow) {
			allowMatchers.add(matcher);
		} else {
			denyMatchers.add(matcher);
		}
	}
}
