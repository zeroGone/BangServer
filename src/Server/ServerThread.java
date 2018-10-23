package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ServerThread implements Runnable {
	private ArrayList<BufferedReader> readers;
	private ArrayList<PrintWriter> writers;
	private ArrayList<String> nickNames;
	private ArrayList<Socket> users;
	private HashMap<String, BufferedReader[]> rooms;
	private Socket user;
	
	public ServerThread() {
		users = new ArrayList<Socket>();
		readers = new ArrayList<BufferedReader>();
		writers = new ArrayList<PrintWriter>();
		nickNames = new ArrayList<String>();
		rooms = new HashMap<String, BufferedReader[]>();
	}
	
	protected void notice(String str) {
		for(int i=0; i<writers.size(); i++) writers.get(i).println(str);
	}
	
	protected void userAdd(Socket user){	
		this.user = user; 
		users.add(user);
		Thread userThread = new Thread(this);
		userThread.start();
	}
	
	private void roomSet() {
		StringBuilder builder = new StringBuilder();
		builder.append("방:");
		Iterator<String> titles = rooms.keySet().iterator();
		while(titles.hasNext()) {
			String title = titles.next();
			BufferedReader[] peoples = rooms.get(title);
			int count = 0;
			for(int i=0; i<peoples.length; i++) {
				if(peoples[i]==null) break;
				count ++;
			}
			builder.append(title+"/"+Integer.toString(count)+",");
		}
		for(int i=0; i<writers.size(); i++) writers.get(i).println(builder.toString());
	}
	
	

	private void nickSet() {
		StringBuilder builder = new StringBuilder();
		builder.append("닉네임:");
		for(int i=0; i<nickNames.size(); i++) builder.append(nickNames.get(i)+",");
		for(int i=0; i<writers.size(); i++) writers.get(i).println(builder.toString().substring(0, builder.length()-1));
	}
	
	@Override 
	public void run() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(user.getInputStream()));
			writers.add(new PrintWriter(user.getOutputStream(),true));
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		readers.add(reader);
		while(ServerMain.status) {
			try {
				String temp = reader.readLine();
				String[] data = temp.split(":");
				switch(data[0]) {
				case "닉네임":
					nickNames.add(data[1]);
					nickSet();
					roomSet();
					break;
				case "방채팅":
					for(int i=0; i<writers.size(); i++) writers.get(i).println("방채팅:"+data[1]);
					break;
				case "방생성":
					BufferedReader[] temps = new BufferedReader[7];
					temps[0] = reader;
					rooms.put(data[1], temps);
					roomSet();
				}
			} catch (IOException e) {
				try {
					for(int i=0; i<readers.size(); i++) 
						if(readers.get(i).equals(reader)) {
							for(int j=0; j<writers.size(); j++) writers.get(j).println("방채팅:"+nickNames.get(i)+",접속 종료");
							nickNames.remove(i);
							readers.remove(i);
							writers.get(i).close();
							writers.remove(i);
							ServerFrame.textArea.append(users.get(i).getInetAddress()+" 접속종료\n");
							users.get(i).close();
							nickSet();
						}
					reader.close();
					break;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} 
		}			
	}
}
