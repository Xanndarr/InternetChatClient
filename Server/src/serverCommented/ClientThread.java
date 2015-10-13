package server_commented;

import java.io.*;
import java.net.*;

//same concept as the client version of the thread.
public class ClientThread extends Thread{
	
	//more variables, you should know about these by now :)
	private BufferedReader in = null;
	private PrintWriter out = null;
	private boolean endConnection = false;
	private String input = null;
	private Socket socket = null;
	private String user = null;
	private String key = null;
        
	//again, the class constructor
    public ClientThread(Socket socketArg, String userArg, String keyArg){
    	//set the args to local variables for use by only this thread
        socket = socketArg;
        user = userArg;
        key = keyArg;
        try{
        	//set the input and output readers again
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
        
    //runs code when the Thread.start() command is called.
    public void run(){
        try{
        	//main thread loop
			do {
				try {
					//if there is something in the input buffer then do this.
					if (in.ready()) {
						//read the line and store it as the input
						input = in.readLine();
						//this is the end connection unicode character. When the client sends this to the server, the server knows that it is disconnecting
						if(input.equals("\u0004")){
							//this stops the main do{}while{} loop
							endConnection = true;
						}else{
							//logging and transmit to other users.
							if(input.startsWith("\u001E") == false){
								Server.logger.info("TXT: User: " + user + ". MSG: " + input);
								System.out.println(user + ": " + input);
								//calls a global function in the main server class that transmits the message received from this one user to every other user.
								Server.relayMessage(input, user);
							}
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				//if the connection hasn't been terminated, sleep for one second to allow other threads to execute
				if(endConnection != true){
					sleep(1000);
				}
			} while (endConnection == false);
			//log the disconnection
			Server.logger.info("User: " + user +  ". Disconnected. --> Key: " + key + " . IP: " + socket.getInetAddress());
			System.out.println("User: " + user +  ". Disconnected. --> Key: " + key + " . IP: " + socket.getInetAddress());
		}catch (Exception e){
			e.printStackTrace();
		//code called after the try{}catch{} has finished running
		}finally{
			try {
				//close all the readers and the socket.
				in.close();
				out.close();
				socket.close();
				//then remove this thread from the threads list stored in the main "Server" class
				Server.removeThread(this.getName());
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
    }
    
    //public function used for sending a message to whoever is connected to this thread.
    //the "user" argument is who sent the message
    public void sendMsg(String user, String msg){
        out.println(user + ": " + msg);
        out.flush();
    }
    
    //this sends the list of people currently connected to the server.
    public void sendConnectionsList(String[] activeUsers){
        for (int i = 0; i < activeUsers.length; i++) {
			out.println("\u001E" + activeUsers[i]);
			out.flush();
		}
    }
    
    //if someone new connects to the server, then this sends the new user information
    public void sendConnection(String newUser, boolean joining){
    	//if the person is joining, then it sends a special character to signify they are joining, then the user's username
    	if(joining == true){
    		out.println("\u001E" + newUser);
        	out.flush();
        //if they are leaving, a different unicode character is sent with their username.
    	}else if(joining == false){
			out.println("\u001F" + newUser);
			out.flush();
		}
    }
}
