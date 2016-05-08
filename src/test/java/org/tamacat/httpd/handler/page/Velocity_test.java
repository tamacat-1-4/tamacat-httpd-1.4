package org.tamacat.httpd.handler.page;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

public class Velocity_test {

	public static void main(String[] args) throws Exception {
		VelocityEngine ve = new VelocityEngine();//PropertyUtils.getProperties("server.properties"));
		ve.setProperty("error.resource.loader.class",
			//"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			"org.apache.velocity.runtime.resource.loader.URLResourceLoader");
		ve.setProperty(
			RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
			      "org.apache.velocity.runtime.log.Log4JLogChute");
		ve.setProperty("runtime.log.logsystem.log4j.logger", "org.tamacat.Test");
		ve.init();
		System.out.println(ve.getTemplate("htdocs/web/index.vm").getData());
	}

}
