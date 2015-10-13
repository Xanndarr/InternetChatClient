package clientCommented;

//imports and shit
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

//main class... you should know about these if you want to be good at object oriented programming! 
public class CreateConnection {
	//Creates new object from the class "RecieveMessage" named recieveThread. atm this just acts as a placeholder, so the object hasn't been created yet!
	RecieveMessage recieveThread = null;
	
	//Creates a new Socket, BufferedReader, PrintWriter and an error number. all set to null since they don't have values yet.
	Socket sock = null;
	BufferedReader in = null;
	PrintWriter out = null;
	private int errStage = 0;
	
	//this is the "Constructor". It is the method(a.k.a. function) that is called as soon as the class object is created.
	//google constructors and destructors... they are basically the first and last pieces of code to be called when an object is instantiated.
	public CreateConnection(String host, int port, String username, String password, String key, JFrame connFrame, JLabel currentOperation, Dimension dim){
		//simple try catch block again....it catches errors instead of letting the program crash.
		try {
			//checks if the key field is null. If it is, then there is nothing to send to the server, so it aborts.
			if(key != null){
				//used to show error messages....this is badly coded and could be done more efficiently by catching different errors etc.
				errStage = 1;
				//sets the socket object that was previously null.
				//this makes a new "Socket" with the parameters, host and port. The host is the Ip address of the server and the port is the port that the server application listens on.
				sock = new Socket(host,port);
				errStage = 2;
				//this allows the program to read data sent by the server socket.
				in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				errStage = 3;
				//allows the program to "print" or send data to the server through the socket.
				out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
				errStage = 4;
				//hides error frame.
				connFrame.setVisible(false);
				//calls the "communicate" method with all those params.
				communicate(username, password, key, connFrame, currentOperation, dim);
			}
		//this catches any exception that may occur.
		} catch (Exception e) {
			// a switch statement is basically a bunch of if statements that work around the same variable.
			//so we pick the variable that we want to test the value of and check if any values match.
			switch (errStage) {
			//if errStage == 1, then do this code etc.....
			case 1:
				currentOperation.setText("<html><p><center>Socket could not be initialised. Are you using the correct server host and port?</center></p></html>");
				//this just shows we have finished writing the code for that case.
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
			//default case....if errStage was equal to none of the above values tested for, then this code is run.
			default:
				currentOperation.setText("<html><p><center>Unknown error occured. Please restart program and try again!</center></p></html>");
				break;
			}
		//this code is run after the try{}catch{} block.
		}finally{
			//allows the user to close the program
			connFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
	}
	
	//private = cannot be called(run) from outside this class
	//void = doens't return a value
	private void communicate(String username,String password, String key, JFrame connFrame, final JLabel currentOperation, final Dimension dim){
		//sets a bunch of variables to do with aesthetics
		//final = its a constant value. i.e. doesn't change throughout the exectution of the code!
		//again, more GUI shit..........
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
		//................up to here
		try {
			//checks if input box is empty
			if (inputField.getText().trim().length() != 0) {
				//if it isn't, run this code....
				//print the input field to the PrintWriter, "out". This inserts the text onto the print writer's buffer.
				out.println(inputField.getText());
				//this effectively sends the data to the server. it pushes the data on the buffer to the other end.
				out.flush();
				//removes the input row on the top to make room for the new message on the bottom.
				model.removeRow(0);
				//this adds a new row in the chat area and adds the text you just sent.
				model.addRow(new Object[]{inputField.getText().trim()});
				//blanks out the input box ready for you to type something else.
				inputField.setText(null);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		//sets the height of each text line to 17 pixels.
		chatArea.setRowHeight(17);
		//adds the text "Welcome to the chat room!" when you are connected to the server.
		model.addRow(new Object[]{"Welcome to the chat room!"});
		//adds the buttons to the chat pane....i.e. more gui stuff
		chatPane.add(chatArea);
		chatPane.add(userConnectionTable);
		chatPane.add(inputField);
		chatPane.add(sendBut);
		chatPane.add(exitBut);
		
		chatFrame.add(chatPane);
		chatFrame.pack();
		chatFrame.setVisible(true);
		//..........
		
		//this tries to send the username, password and the key to the server.
		try {
			//this is encrypted so that no one can intercept messages over the internet.
			//it is encrypted using a simple AES encryption alg I found off the internet.
			out.println(Encryption.encrypt(username));
			out.flush();
			out.println(Encryption.encrypt(password));
			out.flush();
			out.println(key);
			out.flush();
			//this makes a new "local" string.... this string is destroyed once the method has finished executing since it was created within the same method.
			//this is set to whatever the server has previously sent the client.
			//i have coded the server so that this will always be a verification of whether the username the we are trying to log in with is already in use.
			String tempIn = in.readLine();
			//if it is, then we close the program using the "exitProgram" method.
			if(tempIn.trim().equals("Access Denied: A user has already logged in with that nickname!")){
				exitProgram(chatFrame, false, chatFrame, currentOperation, dim);
			}
			//this makes a new thread from the RecieveMessage class.
			//we know it makes a Thread because the RecieveMessage class extends the class, "Thread" which is a class fundamental to the java api itself.
			recieveThread = new RecieveMessage(in, model,model2);
			//this starts the Thread running.
			//a thread is what is used to multitask in Java. (There are probs other ways but idk them :3)
			recieveThread.start();
			//prints a special unicode character and a username (i forgot what the character was, but its used by the server for something or other (also can't remember what lol))
			out.println("\u001E" + username);
			out.flush();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		//again, more action listeners.....
		ActionListener exitAL = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//when this action listener is called, the program exits.
				exitProgram(chatFrame,true, chatFrame, currentOperation, dim);
			}
		};
		ActionListener sendAL = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//when this is called, a message is sent to the server.
				//this is done by called the send() method.
				send(inputField, model);
			}
		};
		//when enter is pressed on the keyboard, it sends a message using the same method, "send()"
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
		
		//adds the action listeners to their respective buttons.
		inputField.addKeyListener(enterKey);
		exitBut.addActionListener(exitAL);
		sendBut.addActionListener(sendAL);
		
	}

	//this exits the program
	public void exitProgram(JFrame chatFrame, boolean exit, JFrame connFrame, JLabel currentOperation, Dimension dim){
		try {
			//sets the variable within the running thread, "quit" to true. since we can't tell where the thread is in its execution, we don't want to stop it immediately in case it causes errors.
			//this is why i have a variable called quit. The thread is coded so that it checks itself whether the user has pressed quit by checking the state of that variable. Then it will safely end itself.
			recieveThread.quit = true;
			//this sends a special character to the server saying that we are disconnecting.
			if(exit == true){
				out.println("\u0004");
				out.flush();
			}
			//this waits until the thread has terminated itself.
			while (recieveThread.proceed == false) {}
			//then it closes the BufferedReader, PrintWriter and finally the socket that is connected to the server.
			in.close();
			out.close();
			sock.close();
		} catch (Exception e1) {
			//if something goes wrong, the error message box is displayed.
			connFrame.setVisible(true);
			currentOperation.setText("<html><p>Error: The server connections failed to close themselves!</p></html>");
		}
		//"disposes" of the frames...this means it closes the gui stuff
		connFrame.dispose();
		chatFrame.dispose();
		//if the user actually wanted to quit, then it will end the program, otherwise, it will restart.
		if(exit == true){
			System.exit(0);
		}else{
			//this calls the main() method in the Client class. This is the first thing that is called whenever the program first starts.
			//we have to pass in the parameter null because of the way java is coded. every main() method always requires the argument "String[] args".... since we don't want to do anything with that, we just pass in a null object.
			Client.main(null);
		}
	}

	//this sends a message to the server.
	public void send(JTextField inputField, DefaultTableModel model){
		try {
			//if the input field isn't blank, it prints the message to the PrintWriter, then sends it to the server.
			if (inputField.getText().trim().length() != 0) {
				out.println(inputField.getText());
				out.flush();
				//then makes the field blank again for the next message.
				inputField.setText(null);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
