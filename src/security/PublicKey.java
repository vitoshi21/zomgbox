package security;

import java.io.Serializable;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;

/** Esta class representa a chave pública de um par de chaves assimétricas.
 */
public class PublicKey extends Key implements Serializable {
    
    PublicKey( String algorithm, java.security.PublicKey key ) throws Exception {
        super( algorithm, key ) ;
    }
    
    /** Constrói uma chave pública a partir da sua representação externa.
     * @param data a representação externa em bytes da chave
     * @throws Exception erro interno
     */
    public static PublicKey createKey( byte[] data ) throws Exception {
        return createKey( "RSA", data ) ;
    }
    public static PublicKey createKey( String algorithm, byte[] data ) throws Exception {
    	java.security.PublicKey key = KeyFactory.getInstance(algorithm).generatePublic( new X509EncodedKeySpec( data) ) ;
    	return new PublicKey( algorithm, key);
    }
}
