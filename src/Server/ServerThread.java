package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

//������� ����� ���� ������
public class ServerThread implements Runnable {
	private ArrayList<BufferedReader> readers;//������� ����� reader���� ������ ArrayList
	private ArrayList<PrintWriter> writers;//�����鿡�� ����� ���� Writer���� ������ ArrayList
	private ArrayList<String> nickNames;//�������� �г����� ������ ArrayList
	private ArrayList<Socket> users;//�������� Socket�� ������ ArrayList
	private ArrayList<Room> rooms;//����� ������ ArrayList
	private Socket user;
	
	public ServerThread() {
		users = new ArrayList<Socket>();
		readers = new ArrayList<BufferedReader>();
		writers = new ArrayList<PrintWriter>();
		nickNames = new ArrayList<String>();
		rooms = new ArrayList<Room>();
	}
	
	protected void notice(String str) {
		for(int i=0; i<writers.size(); i++) writers.get(i).println(str);
	}
	
	protected void userAdd(Socket user) {//�������ι��� ������ ������
		this.user = user; 
		users.add(user);//�߰��ϰ�
		Thread userThread = new Thread(this);
		userThread.start();//�� ������ ������ ����
	}
	
	private void roomSet() {
		StringBuilder builder = new StringBuilder();
		builder.append("��:"+Arrays.toString(rooms.toArray()));
		for(int i=0; i<writers.size(); i++) writers.get(i).println(builder.toString());
	}
	
	private void nickSet() {//����
		StringBuilder builder = new StringBuilder();
		builder.append("�г���:");
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
			e2.printStackTrace();
		}
		readers.add(reader);
		while(ServerMain.status) {
			try {
				String temp = reader.readLine();
				System.out.println(temp);
				String[] data = temp.split(":");
				switch(data[0]) {
				case "�г���"://���� �г��� ����
					nickNames.add(data[1]);//NickNames �߰�
					ServerFrame.textArea.append(user.getInetAddress()+"("+data[1]+")\n");
					nickSet();
					roomSet();
					break;
				case "��ä��":
					for(int i=0; i<writers.size(); i++) writers.get(i).println("��ä��:"+data[1]);
					break;
				case "�����"://���� �����
					data = data[1].split(",");//[������, ����г���]
					ServerFrame.textArea.append(user.getInetAddress()+"("+data[1]+"):"+data[0]+"�̶� �游��\n");
					Room room = new Room(rooms.size()+1, data[0]);
					room.addMember(reader, writers.get(readers.indexOf(reader)), data[1]);
					rooms.add(room);
					roomSet();
					break;
				case "������":
					data = data[1].split(",");
					int roomId = Integer.parseInt(data[0]);
					ServerFrame.textArea.append(user.getInetAddress()+"("+data[1]+"):"+roomId+"���� ����\n");
					for(int i=0; i<rooms.size(); i++)
						if(rooms.get(i).getId()==roomId) 
							rooms.get(i).addMember(reader, writers.get(readers.indexOf(reader)), data[1]);
					roomSet();
					break;
				case "�泪��":
					roomId = Integer.parseInt(data[1]);
					for(int i=0; i<rooms.size(); i++) if(rooms.get(i).getId()==roomId) rooms.get(i).removeMember(reader);
					roomSet();
					break;
				}
			} catch (IOException e) {
				try {
					for(int i=0; i<readers.size(); i++) 
						if(readers.get(i).equals(reader)) {
							for(int j=0; j<writers.size(); j++) writers.get(j).println("��ä��:"+nickNames.get(i)+",���� ����");
							nickNames.remove(i);
							readers.remove(i);
							writers.get(i).close();
							writers.remove(i);
							ServerFrame.textArea.append(users.get(i).getInetAddress()+" ��������\n");
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
