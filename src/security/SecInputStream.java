package security;

import java.io.*;
import java.nio.ByteBuffer;


class SecInputStream
		extends FilterInputStream
{

    private Key cipher ;
    private DataInputStream dis ;
    private ByteBuffer data = ByteBuffer.allocate(0) ;
    
    public SecInputStream( InputStream is, Key cipher ) {
        super(is) ;
        dis = new DataInputStream( is ) ;
        this.cipher = cipher ;
    }
    
    
    public int read() throws IOException {
        this.decodeBlock() ;
        if( data.remaining() > 0 ) {
        	byte b = data.get();
        	return ((int)b) & 0xFF;
        }
        else return -1 ;
    }
    
    public int read( byte[] b ) throws IOException {
        return this.read( b, 0, b.length ) ;
    }
    
    public int read( byte[] b, int offset, int length ) throws IOException {
        this.decodeBlock() ;
        if( data.remaining() > 0 ) {
            int l = Math.min( length, data.remaining() ) ;
            data.get( b, offset, l ) ;
            return l ;
        }
        else return -1 ;
    }
    
    private void decodeBlock() throws IOException {
        if( data.remaining() == 0 ) {
            try {
                byte[] block = new byte[ dis.readShort() ] ;
                dis.readFully( block ) ;
                data = ByteBuffer.wrap( cipher.decrypt( block ) ) ;
            } catch( EOFException x ) {
            }
        }
    }

}
