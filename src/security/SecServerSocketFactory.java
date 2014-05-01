package security;

import java.io.*;
import java.net.*;
import java.rmi.server.*;

public class SecServerSocketFactory
		implements RMIServerSocketFactory
{
	private Key cipher = null;
	
	public SecServerSocketFactory() {
	}

	public SecServerSocketFactory(Key cipher) {
		this.cipher = cipher;
	}
	
	public ServerSocket createServerSocket( int port ) throws IOException {
		if ( cipher == null )
		return new SecServerSocket( port);
		else return new SecServerSocket( port, cipher);
	}

	public boolean equals( Object obj) {
		return (getClass() == obj.getClass() 
						&& this.cipher.equals(((SecServerSocketFactory)obj).cipher));
		
	}
}
