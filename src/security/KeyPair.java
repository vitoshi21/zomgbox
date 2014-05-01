package security;

import java.io.Serializable;
import java.security.*;

/** Esta classe permite gerar pares de chaves destinadas a operações de criptografia assimétrica.
 * Dimensão máxima das mensagens a cficrar: 117 bytes.
 */
final public class KeyPair implements Serializable {
    private PublicKey pubKey ;
    private PrivateKey prvKey ;
    
    /** Cria um par de chaves assimétricas RSA.
     */
    public static KeyPair createKeyPair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA") ;
            kpg.initialize(1024);
            java.security.KeyPair kp = kpg.generateKeyPair() ;
            
            PublicKey pub = new PublicKey( "RSA", kp.getPublic() ) ;
            PrivateKey priv = new PrivateKey( "RSA", kp.getPrivate() ) ;
            
            return new KeyPair( pub, priv);
        }
        catch( Exception x ) {
            x.printStackTrace() ;
        }
        return null;
    }
    
    protected KeyPair( PublicKey pub, PrivateKey priv) {
    	this.pubKey = pub;
    	this.prvKey = priv;
    	
    }

    /** Devolve a chave pública do par.
     * @return a chave pública que compõe o par
     */
    public PublicKey getPublic() {
        return pubKey ;
    }
    
    /** Devolve a chave privada do par.
     * @return a chave privada que compõe o par
     */
    public PrivateKey getPrivate() {
        return prvKey ;
    }
    
}
