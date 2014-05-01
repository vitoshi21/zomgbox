package security ;

import java.io.*;
import java.net.*;

public class SecSocket extends Socket {
  
    /*
     * The cipher used to "encrypt" and "decrypt" each byte sent
     * or received by the socket.
     */
    private Key cipher = null;
  
    /* The InputStream used by the socket. */
    private InputStream in = null;
  
    /* The OutputStream used by the socket */
    private OutputStream out = null;
  
    /* 
     * Constructor for class SecSocket. 
     */
    public SecSocket(Key cipher) throws IOException {
        super();
        this.cipher = cipher;
    }
    public SecSocket() throws Exception {
        super();
        this.cipher = null;
    }
    /* 
     * Constructor for class SecSocket. 
     */
    public SecSocket(String host, int port, Key cipher) throws IOException {
        super(host, port);
        this.cipher = cipher;
    }
    
    public SecSocket(String host, int port) throws IOException {
        super(host, port);
        this.cipher =null;
    }
    
    
    /*
     * Redefine cipher key
     */
    public void setKey( Key cipher ) throws IOException {
    	if (in == null && out == null )
    		this.cipher = cipher;
    	else throw new IOException("setKey: can't change cipher key, streams already open.");
    }
  
	
    /* 
     * Returns a stream of type SecInputStream. 
     */
    public synchronized InputStream getInputStream() throws IOException {
        if (in == null) {
        	if (this.cipher== null) {
        		try {
					this.cipher = SymetricKey.createKey("Trabalho SD 2010");
					return super.getInputStream();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}

            in = new SecInputStream(super.getInputStream(), cipher);
        }
        return in;
    }
  
    /* 
     *Returns a stream of type SecOutputStream. 
     */
    public synchronized OutputStream getOutputStream() throws IOException {
        if (out == null) {
        	if (this.cipher== null) {
        		try {
					this.cipher = SymetricKey.createKey("Trabalho SD 2010");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}

            out = new SecOutputStream(super.getOutputStream(), cipher);
        }
        return out;
    }
}
