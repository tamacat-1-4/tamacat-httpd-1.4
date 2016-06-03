package org.tamacat.httpd.config;

import java.io.IOException;
import java.net.Socket;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyClient;
import org.tamacat.io.RuntimeIOException;
import org.tamacat.util.StringUtils;

public class HttpProxyConfig {

	protected String proxyHost;
	protected int proxyPort;
	protected String username;
	protected String password;
	protected String nonProxyHosts;

	public boolean isDirect() {
		if (StringUtils.isNotEmpty(proxyHost) && proxyPort > 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public HttpClientBuilder setProxy(HttpClientBuilder builder) {
		if (isDirect() == false) {
			HttpHost proxy = new HttpHost(proxyHost, proxyPort);
			if (StringUtils.isNotEmpty(username)) {
				CredentialsProvider credsProvider = new BasicCredentialsProvider();
				credsProvider.setCredentials(new AuthScope(proxy), getCredentials());
				builder.setDefaultCredentialsProvider(credsProvider);
			} else {
				builder.setProxy(proxy);
			}
		}
		return builder;
	}

	public Socket tunnel(HttpHost target) {
		try {
			return new ProxyClient().tunnel(getProxyHttpHost(), target, getCredentials());
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		} catch (HttpException e) {
			throw new RuntimeIOException(e);
		}
	}
	
	public HttpHost getProxyHttpHost() {
		return new HttpHost(proxyHost, proxyPort, "http");
	}
	
	public Socket createProxySocket() {
		try {
			return new Socket(proxyHost, proxyPort);
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}
	
	public Credentials getCredentials() {
		if (StringUtils.isNotEmpty(username)) {
			return new UsernamePasswordCredentials(username, password);
		} else {
			return new UsernamePasswordCredentials("", "");
		}
	}
	
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}
	
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getNonProxyHosts() {
		return nonProxyHosts;
	}

	/**
	 * "localhost|127.0.0.1"
	 * @param nonProxyHosts
	 */
	public void setNonProxyHosts(String nonProxyHosts) {
		this.nonProxyHosts = nonProxyHosts;
	}
	
	public void setProxy() {
		if (isDirect() == false) {
			HttpHost proxy = getProxyHttpHost();
			System.setProperty("http.proxyHost", proxy.getHostName());
			System.setProperty("http.proxyPort", String.valueOf(proxy.getPort()));
			System.setProperty("http.proxyUser", getCredentials().getUserPrincipal().getName());
			System.setProperty("http.proxyPassword", getCredentials().getPassword());
			System.setProperty("http.nonProxyHosts", getNonProxyHosts());
			
			System.setProperty("https.proxyHost", proxy.getHostName());
			System.setProperty("https.proxyPort", String.valueOf(proxy.getPort()));
			System.setProperty("https.proxyUser", getCredentials().getUserPrincipal().getName());
			System.setProperty("https.proxyPassword", getCredentials().getPassword());
			System.setProperty("https.nonProxyHosts", getNonProxyHosts());
		}
	}
}
