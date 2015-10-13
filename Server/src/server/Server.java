package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Server {
    
    private String username = null;
    private String password = null;
    private String key = null;
    private int port = 7002;
    private ServerSocket serverSock = null;
    private Socket sock = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    public static Logger logger = null;
    public static FileHandler fh = null;
    public static SimpleFormatter formatter = null;
    public static ClientThread[] threadArray = null;
    private static ClientThread[] tempArray = null;
    private static String[] usernameArray = null;
    private static String[] tempUsernameArray = null;
    private boolean noAccess = false;
    
    public void run(){
    	do {
    		try{
    			addConnection();
    		}catch(Exception e){
        			try {
						sock.close();
						sock = null;
					} catch (Exception e1) {
						e1.printStackTrace();
					}
        	}
		} while (true);
    }
    
    public void addConnection(){
    	int err = 0;
    	username = null;
    	password = null;
    	key = null;
        try{
            err = 1;
            sock = serverSock.accept();
            err = 2;
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
            err = 3;
            username = Encryption.decrypt(in.readLine());
            password = in.readLine();
            key = in.readLine();
			err = 4;
			if(password.equals(key)){
				logger.info("Connection Accepted: User: " + username +  ". Key: " + key + " . IP: " + sock.getInetAddress());
				System.out.println("Connection Accepted: User: " + username +  ". Key: " + key + " . IP: " + sock.getInetAddress());
				if(threadArray.length != 0){
					for (int i = 0; i < threadArray.length; i++) {
						if (threadArray[i].getName().equals(username)){
							noAccess = true;
							out.println("Access Denied: A user has already logged in with that nickname!");
							out.flush();
							System.out.println("Connection: User: " + username +  ". Key: " + key + " . IP: " + sock.getInetAddress() + " --> Denied due to duplicate username!");
							logger.info("Connection: User: " + username +  ". Key: " + key + " . IP: " + sock.getInetAddress() + " --> Denied due to duplicate username!");
						}
					}
				}
				if(noAccess != true){
					out.println("Accepted!");
					out.flush();
					tempArray = new ClientThread[threadArray.length];
					System.arraycopy(threadArray, 0, tempArray, 0, threadArray.length);
					threadArray = new ClientThread[tempArray.length + 1];
					System.arraycopy(tempArray, 0, threadArray, 0, tempArray.length);
					threadArray[threadArray.length - 1] = new ClientThread(sock, username, key);
					threadArray[threadArray.length - 1].setName(username);
					tempUsernameArray = new String[usernameArray.length];
					System.arraycopy(usernameArray, 0, tempUsernameArray, 0, usernameArray.length);
					usernameArray = new String[tempUsernameArray.length + 1];
					System.arraycopy(tempUsernameArray, 0, usernameArray, 0, tempUsernameArray.length);
					usernameArray[usernameArray.length - 1] = username;
					threadArray[threadArray.length - 1].sendConnectionsList(usernameArray);
					for (int t = 0; t < threadArray.length; t++) {
						if(!threadArray[t].getName().equals(username)){
							threadArray[t].sendConnection(username, true);
						}
					}
					threadArray[threadArray.length - 1].start();
				}
			}
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
    
    public static void main(String[] args){
    	System.out.println("Server loading!");
    	Server server = new Server();
    	logger = Logger.getLogger("MyLog");
    	try {
    		fh = new FileHandler("logs/logFile.log");
    		logger.addHandler(fh);
    		formatter = new SimpleFormatter();
    		fh.setFormatter(formatter);
    		logger.setUseParentHandlers(false);
    		server.serverSock = new ServerSocket(server.port);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	tempArray = new ClientThread[0];
    	threadArray = new ClientThread[0];
    	usernameArray = new String[0];
    	tempUsernameArray = new String[0];
        
        System.out.println("Starting Message Relay Thread");
        logger.info("Starting Message Relay Thread");
        
    	System.out.println("Server Started!");
    	logger.info("Server Started.");
    	
        server.load();
        
        System.out.println("Server Terminated!");
    	logger.info("Server Terminated.");
    	
    	server.endServer();
    }
    
    public void load(){
        try {
			run();
		} catch (Exception e) {
			System.out.println("Server Failed to Initialise!");
		}
    }
    
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
    
    public static void relayMessage(String dataArg, String userArg){
    	try{
            for(int i = 0; i < threadArray.length; i++){
                threadArray[i].sendMsg(userArg, dataArg);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public static void removeThread(String threadId){
    	int stop = 0;
    	ArrayList<ClientThread> gArray = new ArrayList<ClientThread>(Arrays.asList(threadArray));
    	for(int i = 0; i < threadArray.length; i++){
    		if(threadArray[i].getName().equals(threadId)){
    			stop = i;
    		}else{
    			threadArray[i].sendConnection(threadId, false);
    		}
    	}
    	gArray.remove(stop);
    	threadArray = new ClientThread[gArray.size()];
    	threadArray = gArray.toArray(threadArray);
    	
    	stop = 0;
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
