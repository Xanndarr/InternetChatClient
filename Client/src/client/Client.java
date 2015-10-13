package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Client {
	
	String host = null;
	String port = null;
	String username = null;
	String password = null;
	String key = null;
	Scanner fileIn = null;
	Color redColor = new Color(195,30,3);
	Color whiteColor = new Color(255,255,255);
	
	JFrame connFrame = new JFrame("Initialising Connection.");
	JLabel currentOperation = new JLabel("Initialising connection to server.", SwingConstants.CENTER);
	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

	private void logon(){		
		//JFrame.setDefaultLookAndFeelDecorated(true);
		final JFrame window = new JFrame("IRC");
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.setLayout(new FlowLayout(FlowLayout.CENTER));
		window.setLocation(dim.width/2-90, dim.height/2-130);
		JPanel pane = new JPanel();
		pane.setPreferredSize(new Dimension(180, 260));
		window.add(pane);
		
		JLabel label1 = new JLabel("Host:",SwingConstants.CENTER);
		JLabel label2 = new JLabel("Port:",SwingConstants.CENTER);
		JLabel label3 = new JLabel("Username:",SwingConstants.CENTER);
		JLabel label4 = new JLabel("Password:",SwingConstants.CENTER);
		
		final JTextField hostField = new JTextField("192.168.1.107");
		final JTextField portField = new JTextField("7002");
		final JTextField userField = new JTextField();
		final JPasswordField passField = new JPasswordField();
		
		JButton joinButton = new JButton("Join");
		JButton clearButton = new JButton("Clear");
		JButton exitButton = new JButton("Exit");
				
		label1.setPreferredSize(new Dimension(180, 15));
		label2.setPreferredSize(new Dimension(180, 15));
		label3.setPreferredSize(new Dimension(180, 15));
		label4.setPreferredSize(new Dimension(180, 15));
		hostField.setPreferredSize(new Dimension(150,25));
		portField.setPreferredSize(new Dimension(150,25));
		userField.setPreferredSize(new Dimension(150,25));
		passField.setPreferredSize(new Dimension(150,25));
		joinButton.setPreferredSize(new Dimension(150,20));
		clearButton.setPreferredSize(new Dimension(73,20));
		exitButton.setPreferredSize(new Dimension(72,20));
		
		pane.add(label1);
		pane.add(hostField);
		pane.add(label2);
		pane.add(portField);
		pane.add(label3);
		pane.add(userField);
		pane.add(label4);
		pane.add(passField);
		
		pane.add(joinButton);
		pane.add(clearButton);
		pane.add(exitButton);
		
		window.setResizable(false);
		window.pack();
		window.setVisible(true);
		
		JPanel paneC = new JPanel();
		
		paneC.setPreferredSize(new Dimension(600,100));
		connFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		connFrame.setLocation(dim.width/2-300, dim.height/2-300);
		connFrame.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		currentOperation.setPreferredSize(new Dimension(590, 90));
		
		connFrame.add(paneC);
		paneC.add(currentOperation);
		connFrame.pack();
		connFrame.setResizable(false);
		
		try {
			fileIn = new Scanner(new FileReader("password.key"));
			key = fileIn.nextLine();
			fileIn.close();
			connFrame.setVisible(false);
		} catch (FileNotFoundException e1) {
			currentOperation.setText("<html><p><center>Your authentication key file could not be found or it does not contain a key at all. Please contact software developer if you need a new key. [key stored in key.txt]</center></p></html>");
			connFrame.setVisible(true);
			window.dispose();
		}finally{
			connFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
		
		ActionListener aLJoin = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(hostField.getText().trim().length() == 0){
	                hostField.setBackground(redColor);
	            }if(portField.getText().trim().length() == 0){
	                portField.setBackground(redColor);
	            }if(userField.getText().trim().length() == 0){
	                userField.setBackground(redColor);
	            }if(passField.getPassword().length == 0){
	                passField.setBackground(redColor);
	            }if(hostField.getText().trim().length() != 0 && portField.getText().trim().length() != 0 && userField.getText().trim().length() != 0 && passField.getPassword().length != 0){
	            	host = hostField.getText();
					port = portField.getText();
					username = userField.getText();
					password = new String(passField.getPassword());
					if(key != null){
						try {
							if(password.equals(Encryption.decrypt(key))){
								window.dispose();
								connectServer();
							}else{
								passField.setBackground(redColor);
							}
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
	            }
			}
		};
		ActionListener aLClear = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				hostField.setText(null);
				hostField.setBackground(whiteColor);
				portField.setText(null);
				portField.setBackground(whiteColor);
				userField.setText(null);
				userField.setBackground(whiteColor);
				passField.setText(null);
				passField.setBackground(whiteColor);
			}			
		};
		ActionListener aLExit = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				window.dispose();
				System.exit(0);
			}
		};
		KeyListener enterKey = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					if(hostField.getText().trim().length() == 0){
		                hostField.setBackground(redColor);
		            }if(portField.getText().trim().length() == 0){
		                portField.setBackground(redColor);
		            }if(userField.getText().trim().length() == 0){
		                userField.setBackground(redColor);
		            }if(passField.getPassword().length == 0){
		                passField.setBackground(redColor);
		            }if(hostField.getText().trim().length() != 0 && portField.getText().trim().length() != 0 && userField.getText().trim().length() != 0 && passField.getPassword().length != 0){
		            	host = hostField.getText();
						port = portField.getText();
						username = userField.getText();
						password = new String(passField.getPassword());
						if(key != null){
							try {
								if(password.equals(Encryption.decrypt(key))){
									window.dispose();
									connectServer();
								}else{
									passField.setBackground(redColor);
								}
							} catch (Exception e2) {
								e2.printStackTrace();
							}
						}
		            }
				}
			}
		};
		
		hostField.addKeyListener(enterKey);
		portField.addKeyListener(enterKey);
		userField.addKeyListener(enterKey);
		passField.addKeyListener(enterKey);
		joinButton.addActionListener(aLJoin);
		clearButton.addActionListener(aLClear);
		exitButton.addActionListener(aLExit);
	}
	
	private void connectServer(){
		@SuppressWarnings("unused")
		CreateConnection connectionObject = new CreateConnection(host, new Integer(port), username, password, key, connFrame, currentOperation, dim);
	}
	
	public static void main(String[] args) {
            Client cObject = new Client();
            cObject.logon();
	}

}