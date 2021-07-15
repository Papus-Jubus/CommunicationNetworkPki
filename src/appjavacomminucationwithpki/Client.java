/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appjavacomminucationwithpki;

import static appjavacomminucationwithpki.Server.formatter;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.Socket;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.FlowLayout;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JTextField;
/**
 *
 * @author Dell
 */
public class Client extends javax.swing.JFrame implements ActionListener{

    //VARIABLE CLIENTS
      private static Socket clientSocket;
	private static int PORT;
	private PrintWriter sortieMessage;
         private static PublicKey serverPublicKEey;
        
         
         
        
        //JFRAME VARIABLE
        private static JPanel contentPane;
	private static JTextArea txtAreaLogs;
	private JButton btnStart;
	private JPanel panelNorth;
	private JLabel lblChatClient;
	private JPanel panelNorthSouth;
	private JLabel lblPort;
	private JLabel lblName;
        private JLabel lblAdress;
	private JPanel panelSouth;
	private JButton btnSend;
	private JTextField txtMessage;
	private JTextField txtNickname;
	private JTextField txtPort;
        private JTextField txtAddress;
	private String clientName;
        private JTextField txtNameToSend;
        
    /**
     * Creates new form Client
     */
    public Client() {
        initComponents();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 570, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
                contentPane.setBackground(Color.magenta);

		panelNorth = new JPanel();
		contentPane.add(panelNorth, BorderLayout.NORTH);
		panelNorth.setLayout(new BorderLayout(0, 0));
                panelNorth.setBackground(Color.red);

		lblChatClient = new JLabel("WE CHAT,TDSI CHAT");
		lblChatClient.setHorizontalAlignment(SwingConstants.CENTER);
		lblChatClient.setFont(new Font("Tahoma", Font.PLAIN, 40));
		panelNorth.add(lblChatClient, BorderLayout.NORTH);

		panelNorthSouth = new JPanel();
		panelNorth.add(panelNorthSouth, BorderLayout.SOUTH);
		panelNorthSouth.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

		lblName = new JLabel("Nickname");
		panelNorthSouth.add(lblName);

		txtNickname = new JTextField();
		txtNickname.setColumns(8);
		panelNorthSouth.add(txtNickname);
                
                lblAdress = new JLabel("Address");
                panelNorthSouth.add(lblAdress);
                
                txtAddress = new JTextField();
                txtAddress.setColumns(8);
                 panelNorthSouth.add(txtAddress);

		lblPort = new JLabel("Port");
		panelNorthSouth.add(lblPort);

		txtPort = new JTextField();
		panelNorthSouth.add(txtPort);
		txtPort.setColumns(8);

		btnStart = new JButton("START");
		panelNorthSouth.add(btnStart);
		btnStart.addActionListener(this);
		btnStart.setFont(new Font("Tahoma", Font.PLAIN, 12));
                btnStart.setBackground(Color.GREEN);

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		txtAreaLogs = new JTextArea();
		txtAreaLogs.setBackground(Color.BLUE);
		txtAreaLogs.setForeground(Color.WHITE);
		txtAreaLogs.setLineWrap(true);
		scrollPane.setViewportView(txtAreaLogs);

		panelSouth = new JPanel();
		FlowLayout fl_panelSouth = (FlowLayout) panelSouth.getLayout();
		fl_panelSouth.setAlignment(FlowLayout.RIGHT);
		contentPane.add(panelSouth, BorderLayout.SOUTH);

		txtMessage = new JTextField();
		panelSouth.add(txtMessage);
		txtMessage.setColumns(20);
                
                txtNameToSend = new JTextField();
                panelSouth.add(txtNameToSend);
		txtNameToSend.setColumns(10);

		btnSend = new JButton("SEND");
                btnSend.setBackground(Color.GREEN);
		
		btnSend.addActionListener(this);
		btnSend.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panelSouth.add(btnSend);
        
    }

    
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
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Client().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    //DEBUT DES METHODES
    public void start() {
		try {
			PORT = Integer.parseInt(txtPort.getText().trim());
			clientName = txtNickname.getText().trim();
                        String address = txtAddress.getText();
			clientSocket = new Socket(address, PORT);
			sortieMessage = new PrintWriter(clientSocket.getOutputStream(), true);
			new Thread(new Listener()).start();
			//send name
			sortieMessage.println(clientName);
                        
                         //============================================security,On definit la cle public du serveur
            String PublicKeyString = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCgFGVfrY4jQSoZQWWygZ83roKXWD4YeT2x2p41dGkPixe73rT2IW04glagN2vgoZoHuOPqa5and6kAmK2ujmCHu6D1auJhE2tXP+yLkpSiYMQucDKmCsWMnW9XlC5K7OSL77TXXcfvTvyZcjObEz6LIBRzs6+FqpFbUO9SJEfh6wIDAQAB";
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(java.util.Base64.getDecoder().decode(PublicKeyString.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            serverPublicKEey = keyFactory.generatePublic(keySpec);
		} catch (Exception err) {
			addToLogs("[ERROR] "+err.getLocalizedMessage());
		}
	}
    
    	public void stop(){
		if(!clientSocket.isClosed()) {
			try {
				clientSocket.close();
                                System.exit(0);
			} catch (IOException e1) {}
		}
	}

	public static void addToLogs(String message) {
        System.out.printf("%s %s\n", formatter.format(new Date()), message);
        //txtAreaLogs.append(message+" \n");
        txtAreaLogs.append(formatter.format(new Date())+"  ");
        txtAreaLogs.append(message+" \n");
    }
        
         //==========================================SECURITY METHODES
    public static PublicKey serverPublickey(String s) throws InvalidKeySpecException, NoSuchAlgorithmException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(java.util.Base64.getDecoder().decode(s.getBytes()));
        KeyFactory keyFactory = null;
        keyFactory = KeyFactory.getInstance("RSA");
        PublicKey serverPublicKEey = keyFactory.generatePublic(keySpec);

        return serverPublicKEey;

    }

    //METHODE QUI CHIFFRE
    public static byte[] encrytp(String message, PublicKey publickey) {
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

        
        //end methods
        
        //class listener
        private static class Listener implements Runnable {
		private BufferedReader entreeSocket;
		@Override
		public void run() {
			try {
				entreeSocket = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				String read;
				for(;;) {
					read = entreeSocket.readLine();
					if (read != null && !(read.isEmpty())) addToLogs(read);
				}
			} catch (IOException e) {
				return;
			}
		}

	}
    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(e.getSource() == btnStart ) {
             boolean EmptyInfo = txtAddress.getText().isEmpty() && txtNickname.getText().isEmpty() && txtPort.getText().isEmpty();
			if(btnStart.getText().equals("START") && !EmptyInfo) {
				btnStart.setText("STOP");
                                btnStart.setBackground(Color.RED);
				start();
			}else {
				btnStart.setText("START");
                                btnStart.setBackground(Color.GREEN);
				stop();
			}
		}else if(e.getSource() == btnSend && (!txtMessage.getText().isEmpty()|| !txtNameToSend.getText().isEmpty() )) {
			String message = txtMessage.getText().trim();
                         String nomClientCible = txtNameToSend.getText().trim();
                         byte[] clientEncryptedMessage;
                         
			if(!message.isEmpty()) {
                            
                             clientEncryptedMessage = encrytp(message,serverPublicKEey);
                             String messageClient = new Base64().encode(clientEncryptedMessage);
				sortieMessage.println(messageClient);
				txtMessage.setText("");
                                
                               
			} else if(!nomClientCible.isEmpty()){
                            sortieMessage.flush();
                            sortieMessage.println(nomClientCible);
                            txtNameToSend.setText("");
                        }
		}
    }
}
