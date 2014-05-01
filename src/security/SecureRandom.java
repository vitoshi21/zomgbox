package security;

/** Esta classe permite obter geradores seguros de sequências aleatórias de bytes.
 */
final public class SecureRandom {
	
    private java.security.SecureRandom sr ; 
    
    /** Cria um novo gerador com uma semente aleatória.
     */
    public SecureRandom() {
        this( null ) ;
    }
    
    /** Cria um novo gerador dada uma semente, de modo a poder reproduzir a sequência.
     * @param seed uma sequência de bytes que será usada como semente
     */    
    public SecureRandom( byte[] seed ) {
        try {
            sr = java.security.SecureRandom.getInstance( "sha1PRNG") ;
            if( seed != null )
            	sr.setSeed( seed ) ;
        }
        catch( Exception x ) {
            x.printStackTrace() ;
        }    
    }
        
    /** Gera uma sequência de bytes aleatórios.
     * @param count a dimensão em bytes da sequência a gerar
     * @return os bytes da sequencia aleatória
     */    
    public byte[] randomBytes( int count ) {
        byte[] tmp = new byte[ count ] ;
        sr.nextBytes( tmp ) ;
        return tmp ;
    }

}
