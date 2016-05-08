/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.handler.page;

import java.io.StringWriter;
import java.util.Locale;
import java.util.Properties;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * <p>It is the HTTP error page that used Velocity template.
 */
public class VelocityErrorPage {

	static final Log LOG = LogFactory.getLog(VelocityErrorPage.class);

	static final String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";

	static final String DEFAULT_ERROR_HTML
		= "<html><body><p>Error.</p></body></html>";

	protected String charset = "UTF-8";
	protected VelocityEngine velocityEngine;
	protected Properties props;
	protected String templatesPath = "templates";

	public VelocityErrorPage(Properties props) {
		this.props = props;
		init();
	}

	protected void init() {
		try {
			velocityEngine = new VelocityEngine();
			//velocityEngine.setApplicationAttribute(VelocityEngine.RESOURCE_LOADER,
			//        new ClasspathResourceLoader());
			velocityEngine.setProperty("resource.loader", "error");
			velocityEngine.setProperty("error.resource.loader.instance",
					new ClasspathResourceLoader());
			velocityEngine.init(props);
			String path = props.getProperty("templates.path");
			if (path != null) {
				templatesPath = path;
			}
		} catch (Exception e) {
			LOG.warn(e.getMessage());
		}
	}

	public String getErrorPage(
			HttpRequest request, HttpResponse response,
			HttpException exception) {
		VelocityContext context = new VelocityContext();
		return getErrorPage(request, response, context, exception);
	}

	public String getErrorPage(
			HttpRequest request, HttpResponse response,
			VelocityContext context, HttpException exception) {
		response.setStatusCode(exception.getHttpStatus().getStatusCode());
		response.setReasonPhrase(exception.getHttpStatus().getReasonPhrase());

		if (LOG.isTraceEnabled() && exception.getHttpStatus().isServerError()) {
			LOG.trace(exception); //exception.printStackTrace();
		}
		try {
			context.put("url", request.getRequestLine().getUri());
			context.put("method", request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH));
			context.put("exception", exception);

			Template template = getTemplate(
				"error" + exception.getHttpStatus().getStatusCode() + ".vm");
			StringWriter writer = new StringWriter();
			template.merge(context, writer);
			return writer.toString();
		} catch (Exception e) {
			LOG.warn(e.getMessage());
			return getDefaultErrorHtml(exception);
		}
	}

	protected Template getTemplate(String page) throws Exception {
		try {
			return velocityEngine.getTemplate(templatesPath + "/" + page, charset);
		} catch (Exception e) {
			return velocityEngine.getTemplate("templates/error.vm", charset);
		}
	}

	protected String getDefaultErrorHtml(HttpException exception) {
		String errorMessage = exception.getHttpStatus().getStatusCode()
				+ " " + exception.getHttpStatus().getReasonPhrase();
		StringBuilder html = new StringBuilder();
		html.append("<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">");
		html.append("<html><head>");
		html.append("<title>" + errorMessage + "</title>");
		html.append("</head><body>");
		html.append("<h1>" + errorMessage + "</h1>");
		html.append("</body></html>");
		return html.toString();
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
}
