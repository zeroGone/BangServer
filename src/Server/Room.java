package Server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

public class Room extends JFrame{
	public final int ROOM_MEMBER_MAX_NUM = 7;//최대 인원 수
	private final int 방장 = 0;

	private int member;//현재 인원 수

	private JTextArea log;//로그
	private JTextArea titleBar;//타이틀바
	
	private int id;//방 아이디
	private String name;//방 이름
	
	BufferedReader[] readers;//방 멤버들의 reader
	PrintWriter[] writers;//방 멤버들의 writer
	String[] nicks;//방 멤버들의 닉네임들
	
	public void setId(int id) {
		this.id=id;
		this.setTitle(String.format("id:%d, 방제목:%s", this.id, this.name));
		this.write("마이룸:"+id);
		this.revalidate();
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getMember() {
		return this.member;
	}
	
	public Room(int id, String title) {
		setTitle(String.format("id:%d, 방제목:%s", id, title));
		setSize(600,400);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);//종료버튼 눌러도 아무것도안함
		setLayout(null);
		
		//타이틀바 셋팅
		titleBar = new JTextArea();//멤버 수 표시할 텍스트에이리어
		titleBar.setEditable(false);
		titleBar.setBounds(10, 10, 575, 20);
		titleBar.setBorder(new BevelBorder(BevelBorder.RAISED));
		
		//로그 셋팅
		log = new JTextArea();//텍스트가 표기될 JTextArea
		log.setEditable(false);//수정불가
		JScrollPane scroll = new JScrollPane(log);//스크롤패널
		scroll.setBounds(10,40,575,320);//위치 크기지정
		
		add(titleBar);
		add(scroll);
		setVisible(true);
		
		this.id = id;
		this.name = title;
		readers = new BufferedReader[ROOM_MEMBER_MAX_NUM];
		writers = new PrintWriter[ROOM_MEMBER_MAX_NUM];
		nicks = new String[ROOM_MEMBER_MAX_NUM];
	}
	
	public void addMember(BufferedReader reader, PrintWriter writer, String nick) {
		readers[member] = reader;
		writers[member] = writer;
		nicks[member] = nick;
		writer.println("방생성:"+this.id);
		
		this.member = (member+1)%ROOM_MEMBER_MAX_NUM;
		
		this.titleBar.setText(String.format("방인원:%d/7, 방장:%s", member, nicks[방장]));
		this.revalidate();
		
		if(member>=4) writers[0].println("게임:방장준비");
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<nicks.length; i++) {
			if(nicks[i]==null) break;
			builder.append(nicks[i]+",");
		}
		for(int i=0; i<member; i++) writers[i].println("게임:유저추가:"+member+"/"+i+"/"+builder.toString().substring(0,builder.length()-1));
	}
	
	public void removeMember(BufferedReader reader) {
		for(int i=0; i<readers.length; i++) {
			if(readers[i]==null) break;
			if(readers[i].equals(reader)) {
				for(int j=i; j<readers.length-1; j++) {
					readers[j]=readers[j+1];
					writers[j]=writers[j+1];
					nicks[j]=nicks[j+1];
				}
				readers[6] = null; 
				writers[6] = null;
				nicks[6] = null;
				this.member = member-1;
			}
		}
		
		this.titleBar.setText(String.format("방인원:%d/7, 방장:%s", member, nicks[방장]));
		if(member==0) this.dispose();
		this.revalidate();
	}
	
	public void write(String 내용) {
		for(int i=0; i<writers.length; i++) 
			if(writers[i]!=null) writers[i].println(내용);
	}
	
	@Override
	public String toString() {
		return this.id+"/"+this.name+"/"+Integer.toString(member);
	}
}
