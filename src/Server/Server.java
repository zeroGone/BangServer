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
			System.out.println("서버 이미 실행중");
			System.out.println("프로그램 종료");
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
		System.out.println("쓰레드 제거");
	}

	@Override
	public void run() {
		System.out.println("유저 입장 대기중");
		while(status) {
			try {
				Socket user = server.accept();
				ServerFrame.textAppend(user.getInetAddress()+" 접속");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
		System.out.println("끝");
	}
}
