package Server;

import java.io.BufferedReader;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

public class Room extends JFrame{
	public final int ROOM_MEMBER_MAX_NUM = 7;//최대 인원 수
	private int member;//현재 인원 수

	private JTextArea log;//로그
	private JTextArea titleBar;//타이틀바
	
	private int id;//방 아이디
	private String name;//방 이름
	
	BufferedReader[] readers;//방 멤버들의 reader
	PrintWriter[] writers;//방 멤버들의 writer
	String[] nicks;//방 멤버들의 닉네임들
	String 방장;
	
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
	
	public int getId() {
		return this.id;
	}
	
	public void addMember(BufferedReader reader, PrintWriter writer, String nick) {
		readers[member] = reader;
		writers[member] = writer;
		nicks[member] = nick;
		writer.println("방생성:"+this.id);
		
		this.member = (member+1)%7;
		
		//방장이라면
		if(member==1) 방장 = nick;
		
		this.titleBar.setText(String.format("방인원:%d/7, 방장:%s", member, 방장));
		this.revalidate();
	}
	
	public void removeMember(BufferedReader reader) {
		int index = 0; 
		for(int i=0; i<readers.length; i++) {
			if(readers[i]==null) continue;
			if(readers[i].equals(reader)) index = i;
		}
		readers[index] = null;
		writers[index] = null;
		nicks[index] = null;
		this.member = member-1;
		
		if(member==0) this.dispose();
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
