package clientCommented;

//imports...cba commenting these....just google what an import is :P
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

//main class decleration....needed because that's how java works.
public class Client {
	
	//main variable declarations for different things
	//set to null so that a random bit of data from memory isn't used by accident if a value isn't assigned to it before it is used
	String host = null;
	String port = null;
	String username = null;
	String password = null;
	String key = null;
	//this is a Scanner variable....used for pulling inputs from the console or files.
	Scanner fileIn = null;
	//finished colour variables (note: its spelled color, because American't are stupid)
	Color redColor = new Color(195,30,3);
	Color whiteColor = new Color(255,255,255);
	
	//A JFrame is what Java uses to create windows and GUIs
	JFrame connFrame = new JFrame("Initialising Connection.");
	//Anything with J.... in front of it is a subdivision of the JFrame class. This means that anything prefixed "J..." is an aesthetic addition, nothing to do with functionality. :)
	JLabel currentOperation = new JLabel("Initialising connection to server.", SwingConstants.CENTER);
	//gets screen size
	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

	//a "private" function = one which can't be accessed from outside of this "Class"
	//a "void" function = one which doens't return a value
	//logon is the name and since the () is empty, it doesn't take any parameters
	private void logon(){
		//JFrame.setDefaultLookAndFeelDecorated(true);
		//create GUI window
		final JFrame window = new JFrame("IRC");
		//set some boring properties of the window
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.setLayout(new FlowLayout(FlowLayout.CENTER));
		//set on screen location to start application from
		window.setLocation(dim.width/2-90, dim.height/2-130);
		
		//do loads of stuff to the window from here............
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
		//..............to here....this just adds simple labels and buttons and stuff like that to the screen. Nothing interesting and fairly simple :)
		
		//makes a new window used for showing errors if they exist/if they come up
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
		//.....up to here.... (i.e. more boring stuff)
		
		//OKAY! HERE COMES THE JUICY STUFF! haha
		//try-catch block = tries to run a command that normally fails due to unexpected circumstances that cannot be accounted for
		//we "try" and run the code in the try{} section, then if a command fails, the catch{} section runs instead. This is called "catching" the error....i.e. stops the application from crashing
		//so in this instance, if the file "password.key" does not exist, we jump to the catch block
		try {
			//sets the scanner variable from the beginning to start scanning the text file "password.key"
			fileIn = new Scanner(new FileReader("password.key"));
			//sets the String variable, "key" to the first line in the file "password.key"
			key = fileIn.nextLine();
			//closes the scanner, so we don't get a system usage leak/memory leak. google that if you want to know more about it :3
			fileIn.close();
			//hides the error frame since there were no errors
			connFrame.setVisible(false);
		} catch (FileNotFoundException e1) {
			//shows the error screen and tells the user what the error is
			currentOperation.setText("<html><p><center>Your authentication key file could not be found or it does not contain a key at all. Please contact software developer if you need a new key. [key stored in key.txt]</center></p></html>");
			connFrame.setVisible(true);
			window.dispose();
		//this is run after either the try{} or the catch{} section has finished running and will always run.
		}finally{
			//sets the little red x in the right of the window to allow the user to close the application.
			connFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
		
		//This is a keyboard listener. It listens for any inputs from the user.
		//i.e. if the user presses a key on the keyboard, this code is run.
		ActionListener aLJoin = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//checks if any login fields are empty and sets them to red if they are
				if(hostField.getText().trim().length() == 0){
	                hostField.setBackground(redColor);
	            }if(portField.getText().trim().length() == 0){
	                portField.setBackground(redColor);
	            }if(userField.getText().trim().length() == 0){
	                userField.setBackground(redColor);
	            }if(passField.getPassword().length == 0){
	                passField.setBackground(redColor);
	            //if nothing is empty, then it checks the password etc.....
	            }if(hostField.getText().trim().length() != 0 && portField.getText().trim().length() != 0 && userField.getText().trim().length() != 0 && passField.getPassword().length != 0){
	            	//sets the variable, "host" to whatever is in the host text box
	            	//same for port and username
	            	host = hostField.getText();
					port = portField.getText();
					username = userField.getText();
					//same thing for password, but since it is masked with dots, we do it slightly differently
					password = new String(passField.getPassword());
					//checks for a key
					if(key != null){
						//"tries" to decrypt the key and checks if its equal to the password
						//this program works by encrypting the password and storing that locally as a "key"
						//shit method of security, but idc :D
						try {
							if(password.equals(Encryption.decrypt(key))){
								//if password is correct, then it shuts the login screen and run the connect to server function.
								window.dispose();
								connectServer();
							}else{
								//if password is wrong, it sets the password field to red to indicate this.
								passField.setBackground(redColor);
							}
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
	            }
			}
		};
		//another action listener for when someone clears all of the text boxes using the clear button.
		ActionListener aLClear = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//blanks everything and sets it white again
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
		//exits program if someone presses exit button
		ActionListener aLExit = new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				window.dispose();
				System.exit(0);
			}
		};
		//if enter key on keyboard is pressed, it executes the same function as the aLJoin action listener....exactly the same code, so just read those comments.
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
		
		//just simply adds the listeners to their respective buttons so they actually work.
		hostField.addKeyListener(enterKey);
		portField.addKeyListener(enterKey);
		userField.addKeyListener(enterKey);
		passField.addKeyListener(enterKey);
		joinButton.addActionListener(aLJoin);
		clearButton.addActionListener(aLClear);
		exitButton.addActionListener(aLExit);
	}
	
	//private function that doesn't return anything
	private void connectServer(){
		//ignore that, it was just my OCD that made me put that line in :D
		@SuppressWarnings("unused")
		//creates a new Object from the class "CreateConnection"
		//it passes in the parameters: host, port, username, password, key, connFrame, currentOperation and dim.
		//new Integer(port) converts the string "port" to an integer to be used by the next class :)
		CreateConnection connectionObject = new CreateConnection(host, new Integer(port), username, password, key, connFrame, currentOperation, dim);
	}
	
	//this is always the first thing that is called.... just starts the application.
	public static void main(String[] args) {
            Client cObject = new Client();
            cObject.logon();
	}

}