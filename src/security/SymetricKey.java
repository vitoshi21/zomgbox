package security;

import javax.crypto.spec.*;

/** Esta classe engloba as operações de criptografia simétrica
 */
final public class SymetricKey extends Key {
    static private SecureRandom rg = new SecureRandom() ;
    
    /** Constrói um chave simétrica aleatória
     * @throws Exception erro interno
     */
    public static SymetricKey createKey() throws Exception {
    	return createKey( new String( rg.randomBytes(16) ));
    }
    
    /** Constrói uma chave a partir de um segredo.
     * @param secret segredo do qual derivará a chave
     * @throws Exception erro interno
     */
    public static SymetricKey createKey( String secret ) throws Exception {
        return createKey( "AES", secret) ;
    }

    /** Constrói uma chave a partir de um segredo.
     * @param algorithm Algoritmo a usar
     * @param secret Segredo do qual derivará a chave
     * @throws Exception erro interno
     */
    public static SymetricKey createKey( String algorithm, String secret ) throws Exception {
    	SecureRandom rg = new SecureRandom( secret.getBytes()) ;
    	byte [] seed = rg.randomBytes(16);
    	return createKey(algorithm, seed);
    }

    /** Constrói uma chave simétrica a partir da sua representação externa
     * @param key a representação externa da chave em bytes
     * @throws Exception - erro interno
     */
    public static SymetricKey createKey( byte []key ) throws Exception {
    	return createKey( "AES", key);
    }
    public static SymetricKey createKey( String algorithm, byte []key ) throws Exception {
        return new SymetricKey( algorithm, new SecretKeySpec( key, algorithm)) ;
    }

    protected SymetricKey( String algorithm, java.security.Key key ) {
    	super( algorithm, key);
    }
    
}
