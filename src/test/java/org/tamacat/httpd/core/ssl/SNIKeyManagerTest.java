package org.tamacat.httpd.core.ssl;

import static org.junit.Assert.*;

import java.net.URL;
import java.security.KeyStore;
import java.util.Properties;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509ExtendedKeyManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.util.ClassUtils;

public class SNIKeyManagerTest {

	KeyManager[] keymanagers;
	X509ExtendedKeyManager x509KeyManager = null;

	@Before
	public void setUp() throws Exception {
		ServerConfig config = new ServerConfig(new Properties());
		config.setParam("https.keyStoreFile", "sni-test-keystore.jks");
		config.setParam("https.keyPassword", "nopassword");
		config.setParam("https.keyStoreType", "JKS");
		config.setParam("https.protocol", "TLS");
		config.setParam("https.defaultAlias", "test01.example.com");
		
		URL url = ClassUtils.getURL(config.getParam("https.keyStoreFile"));

		KeyStore keystore = KeyStore.getInstance(config.getParam("https.keyStoreType"));
		keystore.load(url.openStream(), config.getParam("https.keyPassword").toCharArray());
		KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmfactory.init(keystore, config.getParam("https.keyPassword").toCharArray());

		keymanagers = kmfactory.getKeyManagers();
		for (KeyManager keyManager : keymanagers) {
			if (keyManager instanceof X509ExtendedKeyManager) {
				x509KeyManager = (X509ExtendedKeyManager) keyManager;
				break;
			}
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testChooseEngineServerAliasStringPrincipalArraySSLEngine() throws Exception {

	}

	@Test
	public void testChooseClientAlias() throws Exception {
	}

	@Test
	public void testChooseServerAlias() throws Exception {
		SNIKeyManager mgr = new SNIKeyManager(x509KeyManager, "test01.example.com");
		
		SSLContext sslcontext = SSLContext.getInstance("TLS");
		sslcontext.init(new KeyManager[] { mgr }, null, null);
		//System.out.println(mgr.chooseServerAlias(null, null, new Socket()));
	}

	@Test
	public void testGetCertificateChain() {
	}

	@Test
	public void testGetClientAliases() {
	}

	@Test
	public void testGetPrivateKey() {
		SNIKeyManager mgr = new SNIKeyManager(x509KeyManager, "test01.example.com");
		assertNotNull(mgr.getPrivateKey("test01.example.com"));
		assertNotNull(mgr.getPrivateKey("test01.localhost"));
		//System.out.println(Base64.getMimeEncoder().encodeToString(mgr.getPrivateKey("test01.localhost").getEncoded()));
		assertNull(mgr.getPrivateKey("test02.example.com"));

	}

	@Test
	public void testGetServerAliases() {
	}

	@Test
	public void testGetCertificateHostname() {
	}

}
