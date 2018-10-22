package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

//�������ο����� ������ ���� �ݰų�
//������ �ޱ⸸��
public class ServerMain implements Runnable{
	private ServerSocket server;
	private Thread serverThread;//������ ���ӵ��� ���� ������
	private ServerThread receiver;//������� ����� Ŭ����
	protected static boolean status;//������ �����ִ��� �˻縦 ���� ����
	private final static int PORT = 2018;
	
	protected void serverOpen() {
		//���� �����ϸ� �������� ���ο� ��ü�� ����
		try {
			server = new ServerSocket(PORT);
		} catch (IOException e) {
			//��Ʈ�� �̹� ���������� ���α׷� ����
			System.out.println("���� �̹� ������");
			System.out.println("���α׷� ����");
			System.exit(0);
		}
		
		status = true;
		
		serverThread = new Thread(this);
		serverThread.start();
		
		receiver = new ServerThread();
	}
	
	protected void serverClose() {
		receiver.notice("����:close");
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		status = false;
	}

	
	
	@Override
	public void run() {
		System.out.println("���� ���� �����");
		//������ ���������� ������ ��� �޴´�
		//status ������ �˻��ϰ� ������ ������ �ݺ����� �������� ������ ����
		while(status) {
			try {
				Socket user = server.accept();
				receiver.userAdd(user);
				ServerFrame.textArea.append(user.getInetAddress()+" ����\n");
			} catch (IOException e) {
				System.out.println("���� ����");
			}
		}
	}
	
}
