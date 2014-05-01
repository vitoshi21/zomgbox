package security;

import java.io.Serializable;

/** Esta representa uma chave criptográfica genérica.
 */
public abstract class Key implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String algorithm ;
    private java.security.Key key;
    
    protected Key( String algorithm, java.security.Key key ) {
        this.algorithm = algorithm ;
        this.key = key ;
    }

    /** Exporta a chave.
     * @return os bytes que compõem a representação externa da chave
     */
    public byte[] exportKey() {
        try {
        	return key.getEncoded();
        } catch( Exception x ) {
            x.printStackTrace() ;
        }
        return null ;
    }
    
    /** Exporta a chave como uma String representando a chave em hexadecimal
     * @return os caracteres hexadecimais que compõem a representação externa da chave
     */
	public String toStringHex() {
		StringBuffer buf = new StringBuffer();
		byte [] arr = this.exportKey();
		for( int i = 0; i < arr.length; i++) {
			int b = arr[i] & 0x00ff; // unsigned byte to int
			if ( b>0xf ) buf.append(  Integer.toHexString( b ) );
			else buf.append( "0"+ Integer.toHexString( b ) );
		}
		return buf.toString();
	}
	
	
    /** Decifra uma mensagem.
     * @param src os bytes que compõem a mensagem
     * @return a mensagem depois de decifrada
     */
    public byte[] decrypt(byte[] src) {
        return decrypt( src, 0, src.length );
    }
    
    /** Decifra uma mensagem
     * @param src os bytes que compões mensagem
     * @param offset início da mensagem
     * @param length comprimento da mensagem
     * @return mensagem depois de decifrada
     */
    public byte[] decrypt(byte[] src, int offset, int length) {
        try {
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance(algorithm) ;
            c.init( javax.crypto.Cipher.DECRYPT_MODE, key ) ;
            return c.doFinal( src, offset, length ) ;
        }
        catch( Exception x ) {
            x.printStackTrace() ;
        }
        return null ;}
    
    /** Cifra uma mensagem.
     * @param src os bytes que compõem a mensagem
     * @return os bytes que compõem a mensagem depois de cifrada
     */
    public byte[] encrypt(byte[] src) {
        return encrypt( src, 0, src.length ) ;
    }
    
    /** Cifra uma mensagem.
     * @param src os bytes que compõem a mensagem
     * @param offset início da mensagem
     * @param length comprimento da mensagem
     * @return os bytes da mensagem depois de cifrada
     */
    public byte[] encrypt(byte[] src, int offset, int length) {
        try {
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance(algorithm) ;
            c.init( javax.crypto.Cipher.ENCRYPT_MODE, key ) ;
            return c.doFinal( src, offset, length ) ;
        }
        catch( Exception x ) {
            x.printStackTrace() ;
        }
        return null ;
    }
}
