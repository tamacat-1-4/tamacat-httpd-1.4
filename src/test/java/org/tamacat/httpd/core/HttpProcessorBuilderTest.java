package org.tamacat.httpd.core;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HttpProcessorBuilderTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddInterceptorHttpRequestInterceptor() {
		HttpProcessorBuilder builder = new HttpProcessorBuilder();
		builder.addInterceptor(new HttpRequestInterceptor() {
			@Override
			public void process(HttpRequest request, HttpContext context)
					throws HttpException, IOException {				
			}
		});
		builder.build();
	}

	@Test
	public void testAddInterceptorHttpResponseInterceptor() {
		HttpProcessorBuilder builder = new HttpProcessorBuilder();
		builder.addInterceptor(new HttpResponseInterceptor() {
			@Override
			public void process(HttpResponse response, HttpContext context)
					throws HttpException, IOException {
			}
		});
		builder.build();
	}

	@Test
	public void testBuild() {
		HttpProcessorBuilder builder = new HttpProcessorBuilder();
		builder.build();
	}

}
