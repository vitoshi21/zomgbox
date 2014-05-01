package security;

import java.io.*;
import java.net.*;
import java.rmi.server.*;

import files.User;

public class SecClientSocketFactory
		implements RMIClientSocketFactory, Serializable
{

	private static final long serialVersionUID = 1L;
	
	private Key key;

	public SecClientSocketFactory() {
	}
	
	public SecClientSocketFactory(Key key) {
		this.key=key;
	}

	public Socket createSocket( String host, int port) throws IOException {
		return new SecSocket( host, port, key);
	}

	public boolean equals( Object obj) {
		return (getClass() == obj.getClass());
	}

}
