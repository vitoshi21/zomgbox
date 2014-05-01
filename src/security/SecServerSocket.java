package security ;

import java.io.*;
import java.net.*;

class SecServerSocket extends ServerSocket {
  
	private Key cipher = null;
	
    public SecServerSocket(int port) throws IOException {
        super(port);
    }
    
    public SecServerSocket(int port, Key cipher) throws IOException {
        super(port);
        this.cipher = cipher;
    }
    
    public Socket accept() throws IOException {
        SecSocket s = new SecSocket(cipher);
        super.implAccept(s);
        
        return s;
    }
}




