/*
 * Copyright (c) 2013, tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.httpd.exception.UnauthorizedException;

public class LdapAuthComponent<T extends AuthUser> extends AbstractAuthComponent<T> {

	protected String DEFAULT_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
	protected static String DEFAULT_LDAP_VERSION = "3";
	protected static String DEFAULT_SECURITY_AUTHENTICATION = "simple";
	protected Hashtable<String, String> env = new Hashtable<>();
	protected String baseDN;

	public LdapAuthComponent() {
		setContextFactory(DEFAULT_CONTEXT_FACTORY);
		setLdapVersion(DEFAULT_LDAP_VERSION);
		setSecurityAuthentication(DEFAULT_SECURITY_AUTHENTICATION);
	}
	
	public void setBaseDN(String baseDN) {
		this.baseDN = baseDN;
	}
	
	public void setContextFactory(String contextFactory) {
		env.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
	}

	//(ldap://localhost/dc=example,dc=com)
	public void setProviderUrl(String providerUrl) {
		env.put(Context.PROVIDER_URL, providerUrl);
	}
	
	public void setSecurityAuthentication(String securityAuthentication) {
		env.put(Context.SECURITY_AUTHENTICATION, securityAuthentication);
	}
	
	public void setLdapVersion(String ldapVersion) {
		env.put("java.naming.ldap.version", ldapVersion);
	}
	
	protected DirContext getDirContext(String uid, String password) {
		try {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			Hashtable<String, String> search = (Hashtable)env.clone();
			search.put(Context.SECURITY_PRINCIPAL , "uid="+uid+","+baseDN);
			search.put(Context.SECURITY_CREDENTIALS , password);
			return new InitialDirContext(search);
		} catch (AuthenticationException e) {
			throw new UnauthorizedException(e.getMessage());
		} catch (NamingException e) {
			throw new ServiceUnavailableException(e);
		}
	}
		
	@Override
	public T getAuthUser(String id, HttpContext context) {
		//getDirContext().search(name, filterExpr, filterArgs, cons);
		return null;
	}

	@Override
	public boolean check(String id, String pass, HttpContext context) {
		if (id != null && pass != null) {
			try {
				DirContext dc = getDirContext(id, pass);
				close(dc);
				return true;
			} catch (UnauthorizedException e) {
				return false;
			}
		}
		return false;
	}
	
	protected void close(DirContext dc) {
		try {
			if (dc != null) dc.close();
		} catch (NamingException e) {
		}
	}
}
