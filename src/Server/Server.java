package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable{
	private ServerSocket server;
	private Thread serverThread;//유저의 접속들을 받은 쓰레드
	private Receiver receiver;//유저들과 통신할 클래스
	protected static boolean status;//서버가 열려있는지 검사를 위한 변수
	private final static int PORT = 2018;
	private ArrayList<Socket> users;
	private ArrayList<PrintWriter> writer;
	
	public Server() {
		users = new ArrayList<Socket>();
		writer = new ArrayList<PrintWriter>();
	}
	
	protected void serverOpen() {
		//서버 실행하면 서버소켓 새로운 객체를 만듬
		try {
			server = new ServerSocket(PORT);
		} catch (IOException e) {
			//포트가 이미 열려있으면 프로그램 종료
			System.out.println("서버 이미 실행중");
			System.out.println("프로그램 종료");
			System.exit(0);
		}
		
		status = true;
		
		serverThread = new Thread(this);
		serverThread.start();
		
		receiver = new Receiver();
	}
	
	protected void serverClose() {
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		status = false;
	}

	@Override
	public void run() {
		System.out.println("유저 입장 대기중");
		//서버가 열려있으면 유저를 계속 받는다
		//status 변수로 검사하고 서버가 닫히면 반복문에 빠져나와 쓰레드 종료
		while(status) {
			try {
				Socket user = server.accept();
				users.add(user);
				receiver.userAdd(user);
				ServerFrame.textArea.append(user.getInetAddress()+" 접속\n");
			} catch (IOException e) {
				System.out.println("서버 닫힘");
			}
		}
	}
	
}
