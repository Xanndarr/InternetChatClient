package server;

import java.io.*;
import java.net.*;

public class ClientThread extends Thread{
	
	private BufferedReader in = null;
	private PrintWriter out = null;
	private boolean endConnection = false;
	private String input = null;
	private Socket socket = null;
	private String user = null;
	private String key = null;
        
    public ClientThread(Socket socketArg, String userArg, String keyArg){
        socket = socketArg;
        user = userArg;
        key = keyArg;
        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
        
    public void run(){
        try{
			do {
				try {
					if (in.ready()) {
						input = in.readLine();
						if(input.equals("\u0004")){
							endConnection = true;
						}else{
							//logging and transmit to other users.
							if(input.startsWith("\u001E") == false){
								Server.logger.info("TXT: User: " + user + ". MSG: " + input);
								System.out.println(user + ": " + input);
								Server.relayMessage(input, user);
							}
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				if(endConnection != true){
					sleep(1000);
				}
			} while (endConnection == false);
			Server.logger.info("User: " + user +  ". Disconnected. --> Key: " + key + " . IP: " + socket.getInetAddress());
			System.out.println("User: " + user +  ". Disconnected. --> Key: " + key + " . IP: " + socket.getInetAddress());
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			try {
				in.close();
				out.close();
				socket.close();
				Server.removeThread(this.getName());
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
    }
    
    public void sendMsg(String user, String msg){
        out.println(user + ": " + msg);
        out.flush();
    }
    
    public void sendConnectionsList(String[] activeUsers){
        for (int i = 0; i < activeUsers.length; i++) {
			out.println("\u001E" + activeUsers[i]);
			out.flush();
		}
    }
    
    public void sendConnection(String newUser, boolean joining){
    	if(joining == true){
    		out.println("\u001E" + newUser);
        	out.flush();
    	}else if(joining == false){
			out.println("\u001F" + newUser);
			out.flush();
		}
    }
}
