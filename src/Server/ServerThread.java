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

//유저들과 통신할 서버 쓰레드
public class ServerThread implements Runnable {
	private ArrayList<BufferedReader> readers;//유저들과 통신할 reader들을 관리할 ArrayList
	private ArrayList<PrintWriter> writers;//유저들에게 통신을 보낼 Writer들을 관리할 ArrayList
	private ArrayList<String> nickNames;//유저들의 닉네임을 저장할 ArrayList
	private ArrayList<Socket> users;//유저들의 Socket을 저장할 ArrayList
	private ArrayList<Room> rooms;//방들을 관리할 ArrayList
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
	
	protected void userAdd(Socket user) {//서버메인받은 유저의 소켓을
		this.user = user; 
		users.add(user);//추가하고
		ServerFrame.textArea.append("유저 인원:"+users.size()+"\n");
		Thread userThread = new Thread(this);
		userThread.start();//그 유저의 쓰레드 시작
	}
	
	private void roomSet() {
		StringBuilder builder = new StringBuilder();
		builder.append("방:"+Arrays.toString(rooms.toArray()));
		for(int i=0; i<writers.size(); i++) writers.get(i).println(builder.toString());
	}
	
	private void nickSet() {//유저
		StringBuilder builder = new StringBuilder();
		builder.append("닉네임:");
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
				case "닉네임"://유저 닉네임 설정
					nickNames.add(data[1]);//NickNames 추가
					ServerFrame.textArea.append(user.getInetAddress()+"의 닉네임:"+data[1]+"\n");
					nickSet();
					roomSet();
					break;
				case "방채팅":
					for(int i=0; i<writers.size(); i++) writers.get(i).println("방채팅:"+data[1]);
					break;
				case "방생성"://유저 방생성
					data = data[1].split(",");//[방제목, 방장닉네임]
					ServerFrame.textArea.append(user.getInetAddress()+"("+data[1]+"):'"+data[0]+"' 방만듬\n");
					Room room = new Room(rooms.size()+1, data[0]);
					room.addMember(reader, writers.get(readers.indexOf(reader)), data[1]);
					rooms.add(room);
					roomSet();
					break;
				case "방입장":
					data = data[1].split(",");
					int roomId = Integer.parseInt(data[0])-1;
					ServerFrame.textArea.append(user.getInetAddress()+"("+data[1]+"):"+(roomId+1)+"번방 접속\n");
					rooms.get(roomId).addMember(reader, writers.get(readers.indexOf(reader)), data[1]);
					roomSet();
					break;
				case "방나감":
					roomId = Integer.parseInt(data[1])-1;
					ServerFrame.textArea.append(user.getInetAddress()+":"+(roomId+1)+"번방 나감\n");
					rooms.get(roomId).removeMember(reader);
					//방 삭제
					if(rooms.get(roomId).getMember()==0) {
						rooms.remove(roomId);
						for(int i=roomId; i<rooms.size(); i++) rooms.get(i).setId(i+1);
					}
					roomSet();
					break;
				}
			} catch (IOException e) {
				try {
					//클라이언트 강종
					//1.강종한 리더가 방에 있는지 검사함
					boolean check = false;
					for(int j=0; j<rooms.size(); j++) {
						if(check) break;
						for(int z=0; z<7; z++) {
							if(rooms.get(j).readers[z]==null) break;
							if(!rooms.get(j).readers[z].equals(reader)) continue;
							//1-1.있으면 제거
							rooms.get(j).removeMember(reader);
							check=true;
						}
					}
					//2.강종한 리더의 인덱스를 찾음
					int index = 0;
					for(int i=0; i<readers.size(); i++) {
						if(readers.get(i).equals(reader)) {
							index = i;
							break;
						}
					}
					//3.접속종료  화면출력
					ServerFrame.textArea.append(users.get(index).getInetAddress()+" 접속종료, 유저인원"+users.size()+"\n");
					//4.클라이언트에게 접속종료했다 닉네임으로 보냄
					for(int j=0; j<writers.size(); j++) writers.get(j).println("방채팅:"+nickNames.get(index)+",접속 종료");
					//5.리더, 라이터, 닉네임, 유저소켓에서 해당 index 제거
					readers.remove(index);
					nickNames.remove(index);
					writers.get(index).close();
					writers.remove(index);
					users.get(index).close();
					users.remove(index);
					//6.닉네임 셋팅
					nickSet();
					//7.리더닫음
					reader.close();
					break;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} 
		}			
	}
}
