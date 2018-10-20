package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{
	private ServerSocket server;
	private Thread thread;
	private boolean status;
	private final static int PORT = 2018;
	
	protected void serverOpen() {
		try {
			server = new ServerSocket(PORT);
		} catch (IOException e) {
			System.out.println("���� �̹� ������");
			System.out.println("���α׷� ����");
			System.exit(0);
		}
		
		thread = new Thread(this);
		thread.start();
		status = true;
	}
	
	protected void serverClose() {
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		status = false;
		System.out.println("������ ����");
	}

	@Override
	public void run() {
		System.out.println("���� ���� �����");
		while(status) {
			try {
				Socket user = server.accept();
				ServerFrame.textAppend(user.getInetAddress()+" ����");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
		System.out.println("��");
	}
}
