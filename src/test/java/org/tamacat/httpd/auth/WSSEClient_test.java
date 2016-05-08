/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class WSSEClient_test {

	/**
	 * @param args
	 * @throws Exception
	 * @throws
	 */
	public static void main(String[] args) throws Exception {
		HttpGet get = new HttpGet("http://localhost/api/test.html");
		get.setHeader("X-WSSE",
			"UsernameToken Username=\"admin\", "
		+ "PasswordDigest=\"9wPf4azc4MBhzCh5HlBU3S9fNdo=\", "
		+ "Nonce=\"MTBjMGE1MTFlNTYwMWVkOQ==\", "
		+ "Created=\"2009-07-18T14:40:53Z\"");
		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response = client.execute(get);
		System.out.println(response.getStatusLine().getStatusCode());
	}
}
