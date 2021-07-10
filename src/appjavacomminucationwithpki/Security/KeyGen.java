/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appjavacomminucationwithpki.Security;

/**
 *
 * @author Jibril
 */
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class KeyGen {
  KeyPairGenerator keyGen;
      KeyPair keypair;
       PrivateKey privateKey;
       PublicKey publicKey;
    
    public KeyGen() throws NoSuchAlgorithmException{
        this.keyGen=KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        this.keypair=keyGen.genKeyPair();
        privateKey = keypair.getPrivate();
        this.publicKey=keypair.getPublic();
        
    }
    
    public PrivateKey getPrivate(){return this.privateKey;}
    public PublicKey  getPublicKey() {return this.publicKey;}
 
    
}
