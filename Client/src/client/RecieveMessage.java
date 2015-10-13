package client;

import java.io.BufferedReader;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

public class RecieveMessage extends Thread{
	
	private BufferedReader in = null;
	public boolean quit = true;
	public boolean proceed = false;
	private DefaultTableModel model = null;
	private DefaultTableModel userModel = null;
	private String inputFromServer = null;
	private ArrayList<String> usernameArray = null;

	public RecieveMessage(BufferedReader inArg, DefaultTableModel modelArg, DefaultTableModel userModelArg){
		if(inArg != null || modelArg != null || userModelArg != null){
			in = inArg;
			model = modelArg;
			userModel = userModelArg;
			quit = false;
			usernameArray = new ArrayList<String>(0);
		}
	}
	
	public void run(){
		boolean missSleep = false;
		while (quit == false) {
			try {
				if(in.ready() == true){
					inputFromServer = in.readLine();
					if(inputFromServer.startsWith("\u001E")){
						usernameArray.add(inputFromServer.trim());
						refreshUserList();
						model.removeRow(0);
						model.addRow(new Object[]{"Server: " + inputFromServer.trim() + " connected!"});
						missSleep = true;
					}else if(inputFromServer.startsWith("\u001F")){
						for (int i = 0; i < usernameArray.size(); i++) {
							if(usernameArray.get(i).equals(inputFromServer.trim())){
								usernameArray.remove(i);
							}
						}
						refreshUserList();						
						model.removeRow(0);
						model.addRow(new Object[]{"Server: " + inputFromServer.trim() + " disconnected!"});
					}else{
						model.removeRow(0);
						model.addRow(new Object[]{inputFromServer.trim()});
					}
				}
				if(missSleep != true){
					sleep(500);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			in.close();
			proceed = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void refreshUserList(){
		userModel.setRowCount(0);
		for(int nr = 0; nr < usernameArray.size() ; nr++){
			userModel.addRow(new Object[]{usernameArray.get(nr)});
		}
	}

}
