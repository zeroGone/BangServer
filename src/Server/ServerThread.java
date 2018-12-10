package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

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
		ServerFrame.textArea.append("���� �ο�:"+users.size()+"\n");
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
				if(temp==null) continue;
				System.out.println(temp);
				String[] data = temp.split(":");
				switch(data[0]) {
				case "�г���"://���� �г��� ����
					nickNames.add(data[1]);//NickNames �߰�
					ServerFrame.textArea.append(user.getInetAddress()+"�� �г���:"+data[1]+"\n");
					nickSet();
					roomSet();
					break;
				case "��ä��":
					for(int i=0; i<writers.size(); i++) writers.get(i).println("��ä��:"+data[1]);
					break;
				case "��":
					String ��� = data[1];
					data = data[2].split(",");
					switch(���) {
					case "����"://���� �����
						ServerFrame.textArea.append(user.getInetAddress()+"("+data[1]+"):'"+data[0]+"' �游��\n");
						Room room = new Room(rooms.size()+1, data[0]);
						room.addMember(reader, writers.get(readers.indexOf(reader)), data[1]);
						rooms.add(room);
						roomSet();
						break;
					case "����":
						int roomId = Integer.parseInt(data[0])-1;
						//���ο� �˻�
						if(rooms.get(roomId).getMember()==7) {//Ǯ���Ͻ�
							//1.������ ������ ã��
							int index = readers.indexOf(reader);
							//2.Ǯ���̶󺸳�
							writers.get(index).println("Ǯ��");
						}else if(rooms.get(roomId).getGameState()) {
							int index = readers.indexOf(reader);
							writers.get(index).println("����������");
						}else{
							//�ƴϸ� ����
							ServerFrame.textArea.append(user.getInetAddress()+"("+data[1]+"):"+(roomId+1)+"���� ����\n");
							rooms.get(roomId).addMember(reader, writers.get(readers.indexOf(reader)), data[1]);
							roomSet();
						}
						break;
					}
					break;
				case "����":
					int roomId = Integer.parseInt(data[2])-1;
					switch(data[1]) {
					case "����":
						ServerFrame.textArea.append(user.getInetAddress()+":"+(roomId+1)+"���濡�� ����\n");
						rooms.get(roomId).removeMember(reader);
						//�� ����
						if(rooms.get(roomId).getMember()==0) {
							rooms.remove(roomId);
							for(int i=roomId; i<rooms.size(); i++) rooms.get(i).setId(i+1);
						}
						roomSet();
						break;
					case "ä��":
						rooms.get(roomId).chatting(reader, data[3]);
						break;
					case "����":
						ServerFrame.textArea.append(roomId+1+"���� ���ӽ���");
						rooms.get(roomId).gameSetting();
						roomSet();
						break;
					case "������":
						rooms.get(roomId).nextTurn();
						break;
					case "ī��":
						if(data.length==6) rooms.get(roomId).userCardUse(reader, data[3], Integer.parseInt(data[5]), data[4].split("/"));
						else rooms.get(roomId).userCardUse(reader, data[3], -1, data[4].split("/"));
						break;
					case "ī������":
						rooms.get(roomId).sendUserCardData(Integer.parseInt(data[3]), Integer.parseInt(data[4]), data[5], data[6]);
						break;
					case "Ĺ���ο�":
						if(data.length==7) rooms.get(roomId).cattleRow(Integer.parseInt(data[4]), data[5], data[6]);
						else rooms.get(roomId).cattleRow(Integer.parseInt(data[4]), data[5], "consume");
						break;
					case "��Ż":
						if(data.length==7) rooms.get(roomId).take(Integer.parseInt(data[3]), Integer.parseInt(data[4]), data[5], data[6]);
						else rooms.get(roomId).take(Integer.parseInt(data[3]), Integer.parseInt(data[4]), data[5], "consume");
						break;
					case "��������":
						rooms.get(roomId).tombSet(data[3].split("/"));
						break;
					case "��������":
						for(int i=0; i<7&&rooms.get(roomId).readers[i]!=null; i++) 
							if(rooms.get(roomId).readers[i].equals(reader)) {
								rooms.get(roomId).cattleRow(i, data[3], "mounting");
								break;
							}
						break;
					case "��":
						rooms.get(roomId).bang(Integer.parseInt(data[3]), Integer.parseInt(data[4]), data[5]);
						break;
					}
					break;
				}
			} catch (IOException e) {
				try {
					//Ŭ���̾�Ʈ ����
					//1.������ ������ �濡 �ִ��� �˻���
					boolean check = false;
					for(int j=0; j<rooms.size(); j++) {
						if(check) break;
						for(int z=0; z<7; z++) {
							if(rooms.get(j).readers[z]==null) break;
							if(!rooms.get(j).readers[z].equals(reader)) continue;
							//1-1.������ ����
							rooms.get(j).removeMember(reader);
							check=true;
						}
					}
					//2.������ ������ �ε����� ã��
					int index = readers.indexOf(reader);
					//3.��������  ȭ�����
					ServerFrame.textArea.append(users.get(index).getInetAddress()+" ��������, �����ο�"+(users.size()-1)+"\n");
					//4.Ŭ���̾�Ʈ���� ���������ߴ� �г������� ����
					for(int j=0; j<writers.size(); j++) writers.get(j).println("��ä��:"+nickNames.get(index)+",���� ����");
					//5.����, ������, �г���, �������Ͽ��� �ش� index ����
					readers.remove(index);
					nickNames.remove(index);
					writers.get(index).close();
					writers.remove(index);
					users.get(index).close();
					users.remove(index);
					//6.�г��� ����
					nickSet();
					//7.��������
					reader.close();
					break;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} 
		}			
	}
}
