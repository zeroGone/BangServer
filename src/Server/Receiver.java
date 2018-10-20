package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class Receiver implements Runnable {
	private ArrayList<Thread> readers;
	private ArrayList<BufferedReader> inputs;
	public Receiver() {
		readers = new ArrayList<Thread>();
	}

	protected void userAdd(Socket user){
		readers.add(new Thread(this));
		inputs.add(new BufferedReader(new InputStreamReader(user.getInputStream());
	}
	
	@Override 
	public void run() {
		BufferedReader input = );

		while(Server.status) {
			System.out.println(readers.size());
			try {
				System.out.println(readers.get(i).readLine());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
