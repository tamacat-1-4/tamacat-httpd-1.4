package org.tamacat.httpd.auth;

import org.apache.http.protocol.BasicHttpContext;

public class LdapAuthComponent_test {

	public static void main(String[] args) throws Exception {
		LdapAuthComponent<AuthUser> ldap = new LdapAuthComponent<>();
		ldap.setProviderUrl("ldap://localhost:10389/");
		String baseDN = "ou=users,ou=system";
		ldap.setBaseDN(baseDN);
		System.out.println(ldap.check("user01", "password", new BasicHttpContext()));
	}
}
