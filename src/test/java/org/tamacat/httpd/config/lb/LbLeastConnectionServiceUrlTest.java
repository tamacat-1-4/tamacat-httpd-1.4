package org.tamacat.httpd.config.lb;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.Test;
import org.tamacat.httpd.config.DefaultReverseUrl;
import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.httpd.config.lb.LbLeastConnectionServiceUrl;
import org.tamacat.httpd.config.lb.SortableReverseUrl;

public class LbLeastConnectionServiceUrlTest {

	@Test
	public void testLbLeastConnectionServiceUrl() throws Exception {
		LbLeastConnectionServiceUrl lb = new LbLeastConnectionServiceUrl();
		ReverseUrl url1 = new DefaultReverseUrl(lb);
		url1.setReverse(new URL("http://localhost:8080/test/"));
		
		ReverseUrl url2 = new DefaultReverseUrl(lb);
		url2.setReverse(new URL("http://localhost:8081/test/"));

		lb.addTarget(url1);
		lb.addTarget(url2);
		
		for (int i=0; i<10; i++) {
			SortableReverseUrl url = (SortableReverseUrl)lb.getReverseUrl();
			assertNotNull(url);
			System.out.println(url.getReverse() +": "+url.getActiveConnections());
		}
	}

}
