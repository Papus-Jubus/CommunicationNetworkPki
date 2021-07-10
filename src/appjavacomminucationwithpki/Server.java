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
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

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
        }
    }

    private static void broadcastMessage(String message) {
        for (PrintWriter p : connectedClients.values()) {
            p.println(message);
        }
    }
    
    //Methode pour envoyer le message a un client specifique
    
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
                String message;
                while ((message = entreeMessage.readLine()) != null) {

                    if (message.equals("Naibey")) {
                        break;
                    }
                     //on recupere le nom du destinataire du message,l'objet du deuxieme clic
                    String nameToSend = entreeMessage.readLine();

                    //envoi du message au client concerne pour le cas de private,a tous les client pour le cas du brodcast methode
                   // broadcastMessage(String.format("[%s] %s", name, message));
                    PrivatetMessage(String.format("[%s] %s", name, message),nameToSend);

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
                addToLogs("LE SERVEUR EST STOPPE...");
                exit = true;
                btnStart.setText("START");
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
