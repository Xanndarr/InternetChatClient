package clientCommented;

import java.io.BufferedReader;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

//this class is a thread. A thread is basically 
public class RecieveMessage extends Thread{
	
	//creates new objects for all of the variables needed. sets them to null since they cannot be given values yet.
	private BufferedReader in = null;
	//note: these next two are public so that they can be accessed/changed from outside of this class.
	public boolean quit = true;
	public boolean proceed = false;
	private DefaultTableModel model = null;
	private DefaultTableModel userModel = null;
	private String inputFromServer = null;
	private ArrayList<String> usernameArray = null;

	//this is the constructor of the class. This is the first block of code called when the object is created in another class.
	public RecieveMessage(BufferedReader inArg, DefaultTableModel modelArg, DefaultTableModel userModelArg){
		//"||" = or
		//"&&" = and
		//"==" = equivalent to
		//"!=" = not equivalent to
		//">=" = greater than or equal to
		//"<=" = less than or equal to
		//"<" and ">" = less than and greater than
		//this makes sure that the arguements have been "passed" actual variables... i.e. makes sure the arguments aren't empty and actually have a value.
		if(inArg != null || modelArg != null || userModelArg != null){
			//sets all local variables to the equivalent arguments passed into the method.
			in = inArg;
			model = modelArg;
			userModel = userModelArg;
			//sets quit to false and creates a new ArrayList for usernames. An ArrayList is basically an array with more built in functionality.
			quit = false;
			usernameArray = new ArrayList<String>(0);
		}
	}
	
	//this is a method that is built into the "Thread" class. Because our current class, "RecieveMessage" *extends* the "Thread" class, we HAVE to implement this method.
	//if we don't, we get errors and we can't actually start the thread.
	//when we ran the command, thread.start() in the CreateConnection class, this is the first thing that is executed.
	public void run(){
		//this is a local variable used to see if the thread should miss the sleep function.
		//it would mean that the while loop would run twice without stopping when first connecting to the server.
		boolean missSleep = false;
		//if the user has quit, then the while loop ends and the program will shut down etc...
		//while the quit variable is false, this code keeps being repeated...........
		while (quit == false) {
			try {
				//if there is some text on the BufferedReader, then carry on....i.e. has the server sent us some data to read?
				if(in.ready() == true){
					//makes this variable equal to the next line of the input buffer. i.e. it equals the next string sent to us from the server.
					inputFromServer = in.readLine();
					//this character is used to prefix all commands from the server that represent a new user logging on to the server. This is how we update the usernames list.
					if(inputFromServer.startsWith("\u001E")){
						//add the new username to the array
						usernameArray.add(inputFromServer.trim());
						//refresh the gui to show the new username using the method "refreshUserList()"
						refreshUserList();
						model.removeRow(0);
						model.addRow(new Object[]{"Server: " + inputFromServer.trim() + " connected!"});
						//because another user has joined, we miss the sleep function....this makes the while loop repeat once more without delay.
						missSleep = true;
					//if the tells us that someone has disconnected, then this removes the username from the username array.
					}else if(inputFromServer.startsWith("\u001F")){
						for (int i = 0; i < usernameArray.size(); i++) {
							if(usernameArray.get(i).equals(inputFromServer.trim())){
								usernameArray.remove(i);
							}
						}
						//refresh the list of users (i.e. update them on the GUI)
						refreshUserList();
						model.removeRow(0);
						model.addRow(new Object[]{"Server: " + inputFromServer.trim() + " disconnected!"});
					//if the server hasn't told us that someone has connected or disconnected, then it must be telling us that someone has sent a message....
					}else{
						//removes the oldest message from the screen.
						model.removeRow(0);
						//adds the new message to the bottom
						model.addRow(new Object[]{inputFromServer.trim()});
					}
				}
				//if we haven't said that we want the thread to loop twice, i.e. miss sleep, then this is called.
				if(missSleep != true){
					//this makes the thread "sleep" for 500 milliseconds. This effectively means that the cpu is allowed 500 milliseconds to execute other code within this java app.
					//we have to include this since CPUs aren't technically capable of multitasking....(well not on this level since this is a single core program)
					//this means we have to allow time for the computer to process other parts of the program.
					sleep(500);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//...................up to here.
		try {
			//after the thread has finished looping...i.e the user has quit, the input reader is closed.
			in.close();
			//we set the variable proceed to true...this tells the "CreateConnection" class that it can go ahead and close the network socket.
			proceed = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//this basically just reloads the array onto the GUI whenever someone logs in or out
	public void refreshUserList(){
		userModel.setRowCount(0);
		for(int nr = 0; nr < usernameArray.size() ; nr++){
			userModel.addRow(new Object[]{usernameArray.get(nr)});
		}
	}

}
