package server_commented;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Server {
    
	//private variables as before, except this time we have a ServerSocket, some loggers and arrays for threads.
    private String username = null;
    private String password = null;
    private String key = null;
    private int port = 7002;
    //a server socket is basically a socket used by a server application lol
    private ServerSocket serverSock = null;
    private Socket sock = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    //these are public so that they can be accessed by other classes
    public static Logger logger = null;
    public static FileHandler fh = null;
    public static SimpleFormatter formatter = null;
    //these are arrays made out of "ClientThread" class objects. They are used to store all threads for active users.
    public static ClientThread[] threadArray = null;
    private static ClientThread[] tempArray = null;
    private static String[] usernameArray = null;
    private static String[] tempUsernameArray = null;
    private boolean noAccess = false;
    
    //this is a simple method that is used to run the server side application. don't confuse this with the other run method used in threads. it is just co-incidental that I have named it run.
    public void run(){
    	//it keeps running this code forever
    	do {
    		try{
    			//calls the method that waits for users to connect to the server
    			addConnection();
    		}catch(Exception e){
        			try {
        				//if there is an error, we close the socket and null it
						sock.close();
						sock = null;
					} catch (Exception e1) {
						e1.printStackTrace();
					}
        	}
		} while (true);
    }
    
    //this is the most important method.
    //this takes all inputs from the client side and checks they are all right, it will then create a new thread for the user if everything is satisfactory.
    //then it will transmit data to ALL the client side users that are connected to the server using the array or threads.
    public void addConnection(){
    	//resets all these vars to null so that the last connection doesn't affect it.
    	int err = 0;
    	username = null;
    	password = null;
    	key = null;
        try{
            err = 1;
            //creates a socket with the client by accepting the connection on the server socket.
            sock = serverSock.accept();
            err = 2;
            //gets the input and output streams from the socket connection with the clientside.
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
            err = 3;
            //decrypts the username to plain text using the same algorithm as with client side.
            username = Encryption.decrypt(in.readLine());
            //password remains encrypted... as does the key.
            password = in.readLine();
            key = in.readLine();
			err = 4;
			//basically, authentication is VERY bad on this server application, so all it does is check if the password is equal to the key.
			// key = encrypted password... i should have used a DB located on the server side, but i couldn't be bothered and at the time, didn't really know what i was doing :3
			if(password.equals(key)){
				//writes that a connection has been accepted to the log file
				logger.info("Connection Accepted: User: " + username +  ". Key: " + key + " . IP: " + sock.getInetAddress());
				//prints this to the terminal.
				System.out.println("Connection Accepted: User: " + username +  ". Key: " + key + " . IP: " + sock.getInetAddress());
				//if the threadArray doesn't contain any threads, then this code won't be executed.
				if(threadArray.length != 0){
					//loops through all threads in the array
					for (int i = 0; i < threadArray.length; i++) {
						//checks if the username is currently in use
						if (threadArray[i].getName().equals(username)){
							//if it is, denies access to the user and tells the client it is in use
							noAccess = true;
							out.println("Access Denied: A user has already logged in with that nickname!");
							out.flush();
							//logs the username collision and prints it to console.
							System.out.println("Connection: User: " + username +  ". Key: " + key + " . IP: " + sock.getInetAddress() + " --> Denied due to duplicate username!");
							logger.info("Connection: User: " + username +  ". Key: " + key + " . IP: " + sock.getInetAddress() + " --> Denied due to duplicate username!");
						}
					}
				}
				//if the username doesn't already exist, then this code is called.
				if(noAccess != true){
					//tells the client that their connection has been accepted.
					out.println("Accepted!");
					out.flush();
					//makes new temporary array equal to the size of the old threadArray.
					//NOTE: this could have been coded with much greater ease if I used ArrayLists.
					tempArray = new ClientThread[threadArray.length];
					//copy the old array into the new array
					System.arraycopy(threadArray, 0, tempArray, 0, threadArray.length);
					//extend the old array by one space
					threadArray = new ClientThread[tempArray.length + 1];
					//copy the old threadArray into the new threadArray which has an extended size....this is done using the intermediatery, "tempArray"
					System.arraycopy(tempArray, 0, threadArray, 0, tempArray.length);
					//add a new clientThread to the array and set the name of that thread to the username of the client connected to it.
					//this makes the computer multitask
					threadArray[threadArray.length - 1] = new ClientThread(sock, username, key);
					threadArray[threadArray.length - 1].setName(username);
					//does the same thing again but with the username array...i.e. copies the old array into a new array with a size one greater than the old array.
					tempUsernameArray = new String[usernameArray.length];
					System.arraycopy(usernameArray, 0, tempUsernameArray, 0, usernameArray.length);
					usernameArray = new String[tempUsernameArray.length + 1];
					System.arraycopy(tempUsernameArray, 0, usernameArray, 0, tempUsernameArray.length);
					usernameArray[usernameArray.length - 1] = username;
					//then we send the list of users connected to the client that has just connected.
					threadArray[threadArray.length - 1].sendConnectionsList(usernameArray);
					//since we know a new user has connected, we have to send the new username to all clients currently connected to the server to notify them that a new person has joined the chatroom
					for (int t = 0; t < threadArray.length; t++) {
						if(!threadArray[t].getName().equals(username)){
							//the send connection method has two parameters, a username and whether or not the client is connecting or disconnecting
							threadArray[t].sendConnection(username, true);
						}
					}
					//this then starts the thread of the new connected user.
					threadArray[threadArray.length - 1].start();
				}
			}
			//we then null the sock so a new connection can be accepted! :D
			sock = null;
        }catch(Exception e){
        	if(err == 1){
        		System.out.println("Could not accept user connection.");
        	}if(err == 2){
        		System.out.println("Input and/or output object(s) failed to initialise.");
        	}if(err == 3){
        		System.out.println("Failed to read input buffer stream.");
        	}if(err == 4){
        		System.out.println("Connection thread for user: " + username + ". IP: " + sock.getInetAddress() + " failed.");
        	}
        }
    }
    
    //this is the main thread...i.e. the first thing that is called when the program loads.
    public static void main(String[] args){
    	System.out.println("Server loading!");
    	//new object from the Server class.
    	Server server = new Server();
    	//this sets up the file logger so that errors and information can be written to a file.......
    	logger = Logger.getLogger("MyLog");
    	try {
    		fh = new FileHandler("logs/logFile.log");
    		logger.addHandler(fh);
    		formatter = new SimpleFormatter();
    		fh.setFormatter(formatter);
    		logger.setUseParentHandlers(false);
    		//......until here
    		//this creates a new ServerSocket that listens for connections and data etc.
    		server.serverSock = new ServerSocket(server.port);
    	//btw this is really bad exception handling...i did it really badly throughout all of this code.... i would go through and fix it all up properly, but i really cba lol :)
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	//initiate all the arrays
    	tempArray = new ClientThread[0];
    	threadArray = new ClientThread[0];
    	usernameArray = new String[0];
    	tempUsernameArray = new String[0];
        
    	//print out that everything is good and working etc.
        System.out.println("Starting Message Relay Thread");
        logger.info("Starting Message Relay Thread");
        
    	System.out.println("Server Started!");
    	logger.info("Server Started.");
    	
    	//run the load method in this class.
        server.load();
        
        //prints that the server has been terminated.
        System.out.println("Server Terminated!");
    	logger.info("Server Terminated.");
    	
    	server.endServer();
    }
    
    //this is a fairly pointless method....it just calls the run() method.
    public void load(){
        try {
			run();
		} catch (Exception e) {
			System.out.println("Server Failed to Initialise!");
		}
    }
    
    //this closes all the sockets and readers safely
    public void endServer(){
    	try {
    		if (in != null) {
    			in.close();
    			in = null;
			}
			if (out != null){
				out.close();
		        out = null;
			}
			if(sock != null){
				sock.close();
		        sock = null;
			}
		} catch (Exception e2) {
			System.out.println("Server connections failed to close correctly.");
		}
    }
    
    //this transmits a message to all connected users.
    public static void relayMessage(String dataArg, String userArg){
    	try{
    		//loops throughall thread and calls the sendMsg() method
            for(int i = 0; i < threadArray.length; i++){
                threadArray[i].sendMsg(userArg, dataArg);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    //this removes a user from the thread arrays. when they have disconnected.
    public static void removeThread(String threadId){
    	int stop = 0;
    	//i should have been using ArrayLists all the way through since they are easier to use for this sort of thing, but i only just found out about them at the stage of coding this section.
    	ArrayList<ClientThread> gArray = new ArrayList<ClientThread>(Arrays.asList(threadArray));
    	//this loops through all the threads in the array and checks for the thread to remove. it then sets that threadId to the variable stop.
    	for(int i = 0; i < threadArray.length; i++){
    		if(threadArray[i].getName().equals(threadId)){
    			stop = i;
    		}else{
    			threadArray[i].sendConnection(threadId, false);
    		}
    	}
    	//removes the thread at position, "stop"
    	gArray.remove(stop);
    	//we then convert the ArrayList back to an array...this could have been easily avoided if, yet again, i used ArrayLists for everything :')
    	threadArray = new ClientThread[gArray.size()];
    	threadArray = gArray.toArray(threadArray);
    	
    	//reset the stop variable so it can be used again
    	stop = 0;
    	//do exactly the same thing but for the list of usernames in the username array
    	ArrayList<String> hArray = new ArrayList<String>(Arrays.asList(usernameArray));
    	for(int j = 0; j < usernameArray.length; j++){
    		if(usernameArray[j].equals(threadId)){
    			stop = j;
    		}
    	}
    	hArray.remove(stop);
    	usernameArray = new String[hArray.size()];
    	usernameArray = hArray.toArray(usernameArray);
    }
    
}
