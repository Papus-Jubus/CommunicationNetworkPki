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
import java.security.PrivateKey;
import java.security.PublicKey;

public class KeyGen {
  public static void main(String[] argv) throws Exception {
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
    keyGen.initialize(1024);
    KeyPair keypair = keyGen.genKeyPair();
    PrivateKey privateKey = keypair.getPrivate();
    System.out.println(privateKey);
    PublicKey publicKey = keypair.getPublic();
    System.out.println(publicKey);
  }
}
