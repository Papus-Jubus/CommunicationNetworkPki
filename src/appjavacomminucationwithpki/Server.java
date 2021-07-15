/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appjavacomminucationwithpki;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;


//LOCAL IMPORTING
import appjavacomminucationwithpki.Security.KeyGen;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import sun.misc.BASE64Decoder;

/**
 *
 * @author Dell
 */
public class Server extends javax.swing.JFrame implements ActionListener {

    //VARIABLES SERVER
    private static int PORT;
    public static SimpleDateFormat formatter = new SimpleDateFormat("[hh:mm a]");
    protected static HashMap<String, PrintWriter> connectedClients = new HashMap<>();
    protected static HashMap<String,PublicKey> connectedClientPrivateKey = new HashMap<>();
    private static final int MAX_CONNECTED = 50;
    private static ServerSocket server;
    private static volatile boolean exit = false;
    //volatile car ,on est en multithrad,la variable est modifie plusieurs fois,or on doit recuperer une valeur a jour d'ou le mot cle volatile

    //SERVER PUBLIC KEY AND PRIVATE KEY IN STRING
    private static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCgFGVfrY4jQSoZQWWygZ83roKXWD4YeT2x2p41dGkPixe73rT2IW04glagN2vgoZoHuOPqa5and6kAmK2ujmCHu6D1auJhE2tXP+yLkpSiYMQucDKmCsWMnW9XlC5K7OSL77TXXcfvTvyZcjObEz6LIBRzs6+FqpFbUO9SJEfh6wIDAQAB";

    private static String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKAUZV+tjiNBKhlBZbKBnzeugpdYPhh5PbHanjV0aQ+LF7vetPYhbTiCVqA3a+Chmge44+prlqd3qQCYra6OYIe7oPVq4mETa1c/7IuSlKJgxC5wMqYKxYydb1eULkrs5IvvtNddx+9O/JlyM5sTPosgFHOzr4WqkVtQ71IkR+HrAgMBAAECgYAkQLo8kteP0GAyXAcmCAkA2Tql/8wASuTX9ITD4lsws/VqDKO64hMUKyBnJGX/91kkypCDNF5oCsdxZSJgV8owViYWZPnbvEcNqLtqgs7nj1UHuX9S5yYIPGN/mHL6OJJ7sosOd6rqdpg6JRRkAKUV+tmN/7Gh0+GFXM+ug6mgwQJBAO9/+CWpCAVoGxCA+YsTMb82fTOmGYMkZOAfQsvIV2v6DC8eJrSa+c0yCOTa3tirlCkhBfB08f8U2iEPS+Gu3bECQQCrG7O0gYmFL2RX1O+37ovyyHTbst4s4xbLW4jLzbSoimL235lCdIC+fllEEP96wPAiqo6dzmdH8KsGmVozsVRbAkB0ME8AZjp/9Pt8TDXD5LHzo8mlruUdnCBcIo5TMoRG2+3hRe1dHPonNCjgbdZCoyqjsWOiPfnQ2Brigvs7J4xhAkBGRiZUKC92x7QKbqXVgN9xYuq7oIanIM0nz/wq190uq0dh5Qtow7hshC/dSK3kmIEHe8z++tpoLWvQVgM538apAkBoSNfaTkDZhFavuiVl6L8cWCoDcJBItip8wKQhXwHp0O3HLg10OEd14M58ooNfpgt+8D8/8/2OOFaR0HzA+2Dm";

    // JFrame Variables
    private static JPanel container;
    private JLabel lblChatServer;
    private JButton btnStart;
    private static JTextArea txtAreaLogs;

    public Server() {
        initComponents();
        setBounds(100, 100, 570, 400);
        container = new JPanel();
        container.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(container);
        container.setLayout(new BorderLayout(0, 0));

        lblChatServer = new JLabel("CHAT SERVER");
        lblChatServer.setHorizontalAlignment(SwingConstants.LEFT);
        lblChatServer.setFont(new Font("Tahoma", Font.PLAIN, 40));

        container.add(lblChatServer, BorderLayout.NORTH);

        btnStart = new JButton("START");
        btnStart.addActionListener(this);
        btnStart.setFont(new Font("Tahoma", Font.PLAIN, 40));

        container.add(btnStart, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane();
        container.add(scrollPane, BorderLayout.CENTER);

        txtAreaLogs = new JTextArea();
        txtAreaLogs.setBackground(Color.BLACK);
        txtAreaLogs.setForeground(Color.WHITE);
        txtAreaLogs.setLineWrap(true);
        scrollPane.setViewportView(txtAreaLogs);
    }

    /**
     * DEBUT DES METHODES UTILES
     *
     */
    public void refreshUIComponents() {
        lblChatServer.setText("CHAT SERVER" + (!exit ? ": " + PORT : ""));
    }

    public static void start() {
        new Thread(new ServerHandler()).start();
    }

    public static void stop() throws IOException {
        if (!server.isClosed()) {
            server.close();
            System.exit(0);
        }
    }

    private static void broadcastMessage(String message) {
        for (PrintWriter p : connectedClients.values()) {
            p.println(message);
        }
    }
    
    //Methode pour envoyer le message a un client specifique,definition canal prive
    
    private static void PrivatetMessage(String message,String name) {
        
        System.out.println(name);
        
        for(String nName : connectedClients.keySet()){
            System.out.println(nName);
            if(nName.equals(name)){
                
                PrintWriter p = connectedClients.get(name);
                p.println(message);
            
            }
        }
    }

    
    public static void addToLogs(String message) {
        System.out.printf("%s %s\n", formatter.format(new Date()), message);
        //txtAreaLogs.append(message+" \n");
        txtAreaLogs.append(formatter.format(new Date())+"  ");
        txtAreaLogs.append(message+" \n");
    }

    private static int getRandomPort() {
        int port = FreePortFinder.findFreeLocalPort();
        PORT = port;
        return port;
    }
    
    
    //======================SECURITY METHODS
    //on definit la cle public du serveur

    public static PublicKey serverPublickey(String s) throws InvalidKeySpecException, NoSuchAlgorithmException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(java.util.Base64.getDecoder().decode(s.getBytes()));
        KeyFactory keyFactory = null;
        keyFactory = KeyFactory.getInstance("RSA");
        PublicKey serverPublicKEey = keyFactory.generatePublic(keySpec);

        return serverPublicKEey;

    }

    //on definit la cle prive du serveur
    public static PrivateKey serverPrivateKey(String s) throws InvalidKeySpecException {
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(s.getBytes()));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }
    
    public static byte[] encrytp(String message,PublicKey publickey) {
        byte[] chiffre = null;
        try {
            
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publickey);
            chiffre = cipher.doFinal(message.getBytes());
            
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return chiffre;
    }
    
      public static String decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data));
       
    }
      
      public static String decrypt1(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data));
    }

    /**
     * FIN DES METHODES
     */
    /**
     * CLASS DE CONVERSATION DU serveur
     */
    public static class ServerHandler implements Runnable {

        @Override
        public void run() {
            try {
                server = new ServerSocket(PORT);
                addToLogs("Server Run at " + PORT);
                addToLogs("Le Serveur TDSI attend les clients");

                //Boucle infini du serveur,qui ecoute l'arrivee de nouveaux clients 
                while (!exit) {
                    new Thread(new ClienHandler(server.accept())).start();
                }
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    /**
     * CLASS DE CONVERSATION DU CLIENT
     */
    public static class ClienHandler implements Runnable {

        private PrintWriter sortieMessage;
        private BufferedReader entreeMessage;
        Socket socket;
        private String name;
        
        //=================SECURITY======================
        KeyGen keyClientGenerate;
        private PrivateKey ClientPrivateKey;
        private PublicKey  ClientPublicKey;

        public ClienHandler(Socket socket) throws NoSuchAlgorithmException {
            this.keyClientGenerate = new KeyGen();
            
            ClientPrivateKey = keyClientGenerate.getPrivate();
            ClientPublicKey = keyClientGenerate.getPublicKey();
            this.socket = socket;
        }

        @Override
        public void run() {

            addToLogs("Client Connecte :->" + socket.getInetAddress());

            try {
                entreeMessage = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                sortieMessage = new PrintWriter(socket.getOutputStream(), true);
                //on entre dans une boucle infinie pour ecouter les threads

                for (;;) {
                    name = entreeMessage.readLine();
                    if (name == null) {
                        return;
                    }
                    //le mot cle synchronized permet d executer le bloc en priorite
                    //en effet ,on verifie si un autre client se connecte avec le meme nom
                    synchronized (connectedClients) {
                        if (!name.isEmpty() && !connectedClients.keySet().contains(name)) {
                            break;
                        } else {
                            sortieMessage.println("INVALIDNAME");
                        }
                    }
                }

                sortieMessage.println("Bienvenue dans Le Chat De TDSI , " + name.toUpperCase());
                broadcastMessage("[SYSTEM] " + name.toUpperCase() + " a Rejoint La TDSI CHAT.");
                connectedClients.put(name, sortieMessage); //on met le client dans la tables des connectes
                connectedClientPrivateKey.put(name, ClientPublicKey);//on enregistre la cle public du client
                sortieMessage.println("Vous pouvez Maintenant Communiquer ");
                  String EncryptedReceivedClientMessageSring;
                
                
                while ((EncryptedReceivedClientMessageSring = entreeMessage.readLine()) != null) {

                    if (EncryptedReceivedClientMessageSring.equals("Naibey")) {
                        break;
                    }
                     //on recupere le nom du destinataire du message,l'objet du deuxieme clic
                    String nameToSend = entreeMessage.readLine();

                    //on transforme le message recu de string a byte
                    byte[] EncryptedClientmessageByte = new BASE64Decoder().decodeBuffer(EncryptedReceivedClientMessageSring);
               
                     System.out.println("Encrypted Message  :"+EncryptedReceivedClientMessageSring);
                    System.out.println("VEncrypted Message Bytes:  "+EncryptedClientmessageByte);
                    String DecryptClientSourceMessage = decrypt(EncryptedClientmessageByte, serverPrivateKey(privateKey));
                    PrivatetMessage(String.format("[%s] %s", name, DecryptClientSourceMessage), nameToSend);
                }
            } catch (Exception e) {
                addToLogs(e.getMessage());
            } finally {
                if (name != null) {
                    addToLogs(name + " a Fini ");
                    connectedClients.remove(name);
                    broadcastMessage(name + "est Parti");
                }
            }

        }

    }
    
    

    
    //VOICI LA METHODE QUI NOUS PERMET DEMARRER LE SERVEUR
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnStart) {
            if (btnStart.getText().equals("START")) {
                exit = false;
                getRandomPort(); //c'est a ce niveau qu'on defini le port avec lequel l'appli ecoute
                start();
                btnStart.setText("STOP");
            } else {
                try {
                    addToLogs("LE SERVEUR EST STOPPE...");
                    exit = true;
                    btnStart.setText("START");
                    stop();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        refreshUIComponents();
    }
    
    
    //FIN DU CODE IMPLEMENTE
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Server().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    



}
