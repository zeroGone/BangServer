package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerThread implements Runnable {
	private ArrayList<BufferedReader> readers;
	private ArrayList<PrintWriter> writers;
	private ArrayList<String> nickNames;
	private ArrayList<Socket> users;
	
	private BufferedReader reader;

	public ServerThread() {
		users = new ArrayList<Socket>();
		readers = new ArrayList<BufferedReader>();
		writers = new ArrayList<PrintWriter>();
		nickNames = new ArrayList<String>();
//		userIndex = 0;
	}
	
	protected void notice(String str) {
		for(int i=0; i<writers.size(); i++) writers.get(i).println(str);
	}
	
	protected void userAdd(Socket user){	
		try {
			reader = new BufferedReader(new InputStreamReader(user.getInputStream()));
			readers.add(reader);
			writers.add(new PrintWriter(user.getOutputStream(),true));
		} catch (IOException e) {
			System.out.println("����");
		}
		
		Thread userThread = new Thread(this);
		userThread.start();
	}

	private void nickSet() {
		StringBuilder builder = new StringBuilder();
		builder.append("�г���:");
		for(int i=0; i<nickNames.size(); i++) builder.append(nickNames.get(i)+",");
		for(int i=0; i<writers.size(); i++) writers.get(i).println(builder.toString().substring(0, builder.length()-1));
	}
	
	@Override 
	public void run() {
		while(ServerMain.status) {
			try {
				String temp = reader.readLine();
				String[] data = temp.split(":");
				switch(data[0]) {
				case "�г���":
					nickNames.add(data[1]);
					nickSet();
					break;
				case "��ä��":
					for(int i=0; i<writers.size(); i++) writers.get(i).println("��ä��:"+data[1]);
					break;
				}
			} catch (IOException e) {
				try {
					for(int i=0; i<readers.size(); i++) 
						if(readers.get(i).equals(reader)) {
							System.out.println(nickNames.get(i));
							nickNames.remove(i);
							readers.remove(i);
							writers.get(i).close();
							writers.remove(i);
							nickSet();
						}
					reader.close();
					System.out.println("���� ����");
					break;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} 
		}			
	}
}
