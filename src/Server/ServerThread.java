package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ServerThread implements Runnable {
	private ArrayList<Thread> readers;
	private ArrayList<PrintWriter> writers;

	private BufferedReader reader;

	private static final String[] 명령어 = {
		"닉네임"	
	};

	public ServerThread() {
		readers = new ArrayList<Thread>();
		writers = new ArrayList<PrintWriter>();
	}
	
	protected void roomChat(String str) {
		for(int i=0; i<writers.size(); i++) writers.get(i).println(str);
	}
	
	protected void notice(String str) {
		for(int i=0; i<writers.size(); i++) writers.get(i).println(str);
	}
	
	protected void userAdd(Socket user){	
		try {
			reader = new BufferedReader(new InputStreamReader(user.getInputStream()));
			writers.add(new PrintWriter(user.getOutputStream(),true));
		} catch (IOException e) {
			System.out.println("여기");
		}
		Thread userThread = new Thread(this);
		userThread.start();
		readers.add(userThread);
	}

	@Override 
	public void run() {
		while(ServerMain.status) {
			try {
				String data = reader.readLine();
				System.out.println(data);
				roomChat(data);
			} catch (IOException e) {
				System.out.println("리더 닫음");
				try {
					reader.close();
					break;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} 
		}			
	}
}
