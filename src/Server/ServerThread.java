package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread implements Runnable{
	private ServerSocket server;
	private final static int PORT = 2018;
	
	public ServerThread() throws IOException {
		server = new ServerSocket(PORT);
		this.run();
	}

	@Override
	public void run() {
		try {
			Socket user = server.accept();
			ServerFrame.textAppend(user.getInetAddress()+"Á¢¼Ó");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
