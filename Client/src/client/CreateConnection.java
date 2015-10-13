package client;

import java.net.*;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
 
import javax.swing.JButton;
import javax.swing.JFrame;  
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;


public class CreateConnection {
	
	RecieveMessage recieveThread = null;
	
	Socket sock = null;
	BufferedReader in = null;
	PrintWriter out = null;
	private int errStage = 0;

	public CreateConnection(String host, int port, String username, String password, String key, JFrame connFrame, JLabel currentOperation, Dimension dim){
				
		try {
			if(key != null){
				errStage = 1;
				sock = new Socket(host,port);
				errStage = 2;
				in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				errStage = 3;
				out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
				errStage = 4;
				connFrame.setVisible(false);
				communicate(username, password, key, connFrame, currentOperation, dim);
			}
		} catch (Exception e) {
			switch (errStage) {
			case 1:
				currentOperation.setText("<html><p><center>Socket could not be initialised. Are you using the correct server host and port?</center></p></html>");
				break;
			case 2:
				currentOperation.setText("<html><p><center>ObjectInputStream failed to initialise. Please restart program and try again. If the problem persists, contact the server owner.</center></p></html>");
				break;
			case 3:
				currentOperation.setText("<html><p><center>ObjectOutputStream failed to initialise. Please restart program and try again. If the problem persists, contact the server owner.</center></p></html>");
				break;
			case 4:
				currentOperation.setText("<html><p><center>For some reason the \"communicate() \" method failed to initialise. Please contact the software developer if the problem persists.</center></p></html>");
				break;
			default:
				currentOperation.setText("<html><p><center>Unknown error occured. Please restart program and try again!</center></p></html>");
				break;
			}
		}finally{
			connFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
	}
	
	private void communicate(String username,String password, String key, JFrame connFrame, final JLabel currentOperation, final Dimension dim){
		final JFrame chatFrame = new JFrame("Chat away......");
		final DefaultTableModel model = new DefaultTableModel(34, 1);
		final DefaultTableModel model2 = new DefaultTableModel(0, 1);
		final JTable userConnectionTable = new JTable(model2);
		final JTable chatArea = new JTable(model);
		JPanel chatPane = new JPanel();
		JButton exitBut = new JButton("Exit");
		JButton sendBut = new JButton("Send");
		final JTextField inputField = new JTextField();
		chatFrame.setResizable(false);
		
		chatPane.setPreferredSize(new Dimension(800,630));
		chatFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		chatFrame.setLocation(dim.width/2-400, dim.height/2-300);
		chatFrame.setLayout(new FlowLayout(FlowLayout.LEFT));
		exitBut.setPreferredSize(new Dimension(70, 20));
		sendBut.setPreferredSize(new Dimension(70, 20));
		inputField.setPreferredSize(new Dimension(640,20));
		inputField.setAlignmentX(JFrame.LEFT_ALIGNMENT);
		
		userConnectionTable.setPreferredSize(new Dimension(145,595));
		userConnectionTable.setEnabled(false);
		userConnectionTable.setRowHeight(17);
		chatArea.setPreferredSize(new Dimension(640,595));
		chatArea.setEnabled(false);
		try {
			if (inputField.getText().trim().length() != 0) {
				out.println(inputField.getText());
				out.flush();
				model.removeRow(0);
				model.addRow(new Object[]{inputField.getText().trim()});
				inputField.setText(null);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		chatArea.setRowHeight(17);
		
		model.addRow(new Object[]{"Welcome to the chat room!"});
		
		chatPane.add(chatArea);
		chatPane.add(userConnectionTable);
		chatPane.add(inputField);
		chatPane.add(sendBut);
		chatPane.add(exitBut);
		
		chatFrame.add(chatPane);
		chatFrame.pack();
		chatFrame.setVisible(true);
		
		try {
			out.println(Encryption.encrypt(username));
			out.flush();
			out.println(Encryption.encrypt(password));
			out.flush();
			out.println(key);
			out.flush();
			String tempIn = in.readLine();
			if(tempIn.trim().equals("Access Denied: A user has already logged in with that nickname!")){
				exitProgram(chatFrame, false, chatFrame, currentOperation, dim);
			}
			recieveThread = new RecieveMessage(in, model,model2);
			recieveThread.start();
			out.println("\u001E" + username);
			out.flush();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		ActionListener exitAL = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exitProgram(chatFrame,true, chatFrame, currentOperation, dim);
			}
		};
		ActionListener sendAL = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				send(inputField, model);
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
					send(inputField, model);
				}
			}
		};
		
		inputField.addKeyListener(enterKey);
		exitBut.addActionListener(exitAL);
		sendBut.addActionListener(sendAL);
		
	}
	
	public void exitProgram(JFrame chatFrame, boolean exit, JFrame connFrame, JLabel currentOperation, Dimension dim){
		try {
			recieveThread.quit = true;
			if(exit == true){
				out.println("\u0004");
				out.flush();
			}
			while (recieveThread.proceed == false) {}
			in.close();
			out.close();
			sock.close();
		} catch (Exception e1) {
			connFrame.setVisible(true);
			currentOperation.setText("<html><p>Error: The server connections failed to close themselves!</p></html>");
		}
		connFrame.dispose();
		chatFrame.dispose();
		if(exit == true){
			System.exit(0);
		}else{
			Client.main(null);
		}
	}

	public void send(JTextField inputField, DefaultTableModel model){
		try {
			if (inputField.getText().trim().length() != 0) {
				out.println(inputField.getText());
				out.flush();
				inputField.setText(null);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
