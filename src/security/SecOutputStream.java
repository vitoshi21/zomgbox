package security ;

import java.io.*;
import java.nio.ByteBuffer;


class SecOutputStream extends FilterOutputStream {

    private Key cipher ;
    private DataOutputStream dos ;
    private ByteBuffer block = ByteBuffer.allocate( 992 ) ;
    
    public SecOutputStream( OutputStream os, Key cipher) {
        super(os) ;
        this.cipher = cipher ;
        this.dos = new DataOutputStream( os ) ;
    }
    
    public void write( int b ) throws IOException {
        block.put( (byte)b ) ;
        if( block.remaining() == 0 ) flush() ;
    }
    
    public void flush() throws IOException {
        if( block.position() > 0 ) {
            byte[] data = cipher.encrypt( block.array(), 0, block.position() ) ;
            dos.writeShort( data.length ) ;
            dos.write( data ) ;
            dos.flush() ;
            block.rewind() ;
        }
    }
    
    public void close() throws IOException {
        this.flush() ;
        dos.close() ;
    }
    
}
