package org.tamacat.httpd;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHttpRequest;
import org.tamacat.httpd.config.HttpProxyConfig;
import org.tamacat.httpd.util.ReverseUtils;

public class HttpsClient_test {

	public static void main(String[] args) throws Exception {
		HttpProxyConfig proxy = new HttpProxyConfig();
		//proxy.setProxyHost("localhost");
		//proxy.setProxyPort(3128);

		CloseableHttpClient client = proxy.setProxy(HttpClients.custom())
				.setSSLSocketFactory(ReverseUtils.createSSLSocketFactory("TLS")).build();
		HttpRequest req = new BasicHttpRequest("GET", "https://localhost/ex/");
		HttpResponse resp = client.execute(new HttpHost("localhost", 443, "https"), req);
		System.out.println(resp.getStatusLine());
	}
}
