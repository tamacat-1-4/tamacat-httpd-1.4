/*
 * Copyright (c) 2009, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import java.io.InputStream;
import java.security.KeyStore;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.tamacat.httpd.config.ServerConfig;

public class ClientCert_test {

	static KeyStore keyStore;
	static KeyStore trustStore;

	public static void main(String[] args) throws Exception {
		HttpGet get = new HttpGet("https://localhost/docs/index.html");
		HttpClientBuilder client = HttpClientBuilder.create();

		ServerConfig config = new ServerConfig(new Properties());
		config.setParam("https.keyStoreFile", "test.keystore");
		config.setParam("https.keyPassword", "nopassword");
		config.setParam("https.keyStoreType", "JKS");
		config.setParam("https.protocol", "SSLv3");

		//SSLContextCreator ssl = new SSLContextCreator(config);
		//SSLContext ctx = ssl.getSSLContext();

//		ProxySelectorRoutePlanner routePlanner = new ProxySelectorRoutePlanner(
//				client.getConnectionManager().getSchemeRegistry(),
//				ProxySelector.getDefault());
//		client.setRoutePlanner(routePlanner);

		//SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
//		socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		//DefaultSchemePortResolver sch = new DefaultSchemePortResolver();
		//sch.("https", 443, socketFactory);
		//CloseableHttpClient c = client.build();
		//getConnectionManager().getSchemeRegistry().register(sch);
		HttpResponse response = client.build().execute(get);
		System.out.println(response.getStatusLine().getStatusCode());
	}

	static void load() throws Exception {
		trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
		InputStream instream = ClientCert_test.class.getResourceAsStream("/test.truststore");
		try {
			trustStore.load(instream, "password".toCharArray());
		} finally {
			try {
				instream.close();
			} catch (Exception ignore) {
			}
		}

		keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		instream = ClientCert_test.class
				.getResourceAsStream("/test.keystore");
		try {
			keyStore.load(instream, "password".toCharArray());
		} finally {
			try {
				instream.close();
			} catch (Exception ignore) {
			}
		}
	}
}
