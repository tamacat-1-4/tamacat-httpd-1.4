package org.tamacat.httpd.handler.page;

import static org.junit.Assert.*;

import java.util.Properties;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHttpRequest;
import org.junit.Test;
import org.tamacat.httpd.exception.NotFoundException;
import org.tamacat.httpd.handler.page.VelocityPage;
import org.tamacat.httpd.mock.HttpObjectFactory;
import org.tamacat.util.PropertyUtils;

public class VelocityPageTest {

	@Test
	public void testGetTemplate() throws Exception {
		Properties props = PropertyUtils.getProperties("velocity.properties");
		VelocityPage page = new VelocityPage(props);
		page.init("./src/test/resources/htdocs/root");
		assertNotNull(page.getTemplate("/index.vm"));
	}

	@Test
	public void testGetPage() throws Exception {
		HttpRequest request = new BasicHttpRequest("GET", "/test.html");
		HttpResponse response = HttpObjectFactory.createHttpResponse(200, "OK");

		Properties props = PropertyUtils.getProperties("velocity.properties");
		VelocityPage page = new VelocityPage(props);
		page.init("./src/test/resources/htdocs/root");
		String html = page.getPage(request, response, "/index");
		assertNotNull(html);

		try {
			page.getPage(request, response, "/xxxxxxxxxx");
			fail();
		} catch (NotFoundException e) {
			//OK
		}
	}
}
