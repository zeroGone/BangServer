package Server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
	protected BufferedReader[] readers;//방 멤버들의 reader
	private PrintWriter[] writers;//방 멤버들의 writer
	private String[] nicks;//방 멤버들의 닉네임들
	private String[] userCharacters;//유저의 캐릭터들
	private String[] userJob;
	private int[] userLife;
	private ArrayList<Card> deck;
	private int deckCount;
	
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
	
	//게임 세팅
	public void gameSetting() {
		//유저캐릭터 설정
		this.userCharacters = new String[this.ROOM_MEMBER_MAX_NUM];
		String[] characters = {
				"럭키듀크","로즈둘란","바트캐시디","벌쳐샘","블랙잭","수지라파예트","슬랩더킬러","시드케첨",
				"엘그링고","윌리더키드","제시존스","주르도네","캘러미티자넷","키트칼슨","페드로라미네즈","폴레그렛"
		};
		Boolean[] check = new Boolean[16];
		
		//유저직업 설정
		this.userJob = new String[this.ROOM_MEMBER_MAX_NUM];
		ArrayList<String> job = new ArrayList<String>();
		job.add("보안관");
		job.add("무법자");
		job.add("무법자");
		job.add("배신자");
		if(member>=5) job.add("부관");
		if(member>=6) job.add("무법자");
		if(member==7) job.add("부관");
		Collections.shuffle(job);
		
		for(int i=0; readers[i]!=null||i<7; i++) {
			userJob[i]=job.get(i);
			int random = (int)(Math.random()*16);
			while(check[random]) random = (int)(Math.random()*16);
			userCharacters[i]=characters[random];
			check[random] = true;
		}
		
		//케릭설정 보냄
		StringBuilder builder = new StringBuilder();
		for(int i=0; readers[i]!=null||i<7; i++) builder.append(userCharacters[i]+",");
		this.write(String.format("게임:케릭설정:%s", builder.toString().substring(0, builder.length()-1)));
		
		//생명설정 보냄
		userLife = new int[]{-1,-1,-1,-1,-1,-1,-1};
		//직업설정 보냄
		for(int i=0; readers[i]!=null||i<7; i++) {
			int life = 4;
			if(userCharacters[i].equals("폴레그렛")||userCharacters[i].equals("엘그링고")) life--;
			if(userJob[i].equals("보안관")) {
				this.write("게임:보안관:"+nicks[i]);//보안관 설정
				life++;
			}
			userLife[i]=life;
			writers[i].println("게임:직업:"+userJob[i]);
		}
		
		this.write(String.format("게임:생명:%s",Arrays.toString(userLife)));

    	//덱 셋팅 시작
    	deck = new ArrayList<Card>();
    	for(int i=1; i<=13; i++) {
    		//뱅카드 추가
    		deck.add(new Card("뱅","다이아",i));
    		if(i==1||i>=12) deck.add(new Card("뱅","하트",i));
    		if(i>=2&&i<=9) deck.add(new Card("뱅","클로버",i));
    		//빗나감 추가
    		if(i>=2&&i<=8) deck.add(new Card("빗나감","하트",i));
    		if(i>=10||i==1) deck.add(new Card("빗나감", "클로버", i));
    		//맥주 추가
    		if(i>=6&&i<=11) deck.add(new Card("맥주", "하트", i));
    		if(i==1) {
    			deck.add(new Card("뱅","스페이스",i));//뱅카드 추가
    			deck.add(new Card("카빈", "클로버", i));//카빈 추가
    			deck.add(new Card("조준경", "하트", i));//조준경 추가
    		}else if(i==2) deck.add(new Card("다이너마이트", "하트", i));//다이너마이트 추가
    		else if(i==4) deck.add(new Card("감옥", "하트", i));//감옥 추가
    		else if(i==5) deck.add(new Card("주점", "하트", i));//주점 추가
    		else if(i==8) {
    			deck.add(new Card("결투", "클로버", i));//결투 추가
    			deck.add(new Card("강탈", "다이아", i));//강탈추가
    			deck.add(new Card("윈체스터", "하트", i));//윈체스터 추가
    		}else if(i==9) {
    			//잡화점 추가
    			deck.add(new Card("잡화점", "클로버", i));
    			//역마차 추가
    			deck.add(new Card("역마차", "스페이스", i));
    			deck.add(new Card("역마차", "스페이스", i));
    		}else if(i==10) {
    			//기관총 추가
    			deck.add(new Card("기관총", "하트", i));
    			//볼캐닉추가
    			deck.add(new Card("볼캐닉", "클로벌", i));
    			deck.add(new Card("볼캐닉", "하트", i));
    		}else if(i==11) deck.add(new Card("결투", "스페이스", i));//결투 추가
    		else if(i==12) {
    			deck.add(new Card("결투", "다이아", i));
    			deck.add(new Card("잡화점", "스페이스", i));//잡화점 추가
    		}else if(i==13) {
    			deck.add(new Card("캣벌로우", "하트", i));
    			deck.add(new Card("스코필드", "스페이스", i));
    			deck.add(new Card("레밍턴", "클로버", i));//레밍턴 추가
    		}
    		//인디언 추가
    		if(i==1||i==13) deck.add(new Card("인디언", "다이아", i));
    		//강탈 추가
    		if(i==1||i==11||i==12) deck.add(new Card("강탈", "하트", i));
    		//캣 벌로우 추가
    		if(i>=9&&i<=11) deck.add(new Card("캣벌로우", "다이아", i));
    		//웰스파고은행 추가
    		if(i==3) deck.add(new Card("웰스파고은행", "하트", i));
    		//스코필드 추가
    		if(i==11||i==12) deck.add(new Card("스코필드", "클로버", i));
    		//야생마 추가
    		if(i==8||i==9) deck.add(new Card("야생마", "하트", i));
    		//술통 추가
    		if(i==12||i==13) deck.add(new Card("술통", "스페이스", i));
    		//감옥 추가
    		if(i==10||i==11) deck.add(new Card("감옥", "스페이스", i));
    	}//덱 셋팅 끝
    	//셔플
    	Collections.shuffle(deck);
    	
    	//카드설정 보냄
    	//"카드설정:이름 기호숫자,이름 기호숫자,~"
    	builder.delete(0, builder.length());
    	for(int i=0; userLife[i]!=-1||i<7; i++) {
    		for(int j=0; j<userLife[i]; j++) builder.append(deck.get(deckCount++)+",");
    		writers[i].println("카드설정:"+builder.toString().substring(0,builder.length()-1));
        	builder.delete(0, builder.length());
    	}
	}
	
	//게임 채팅
	public void chatting(BufferedReader reader, String 내용) {
		int index =0; 
		for(int i=0; readers[i]!=null||i<this.ROOM_MEMBER_MAX_NUM; i++) if(readers[i].equals(reader)) index = i;
		this.write(String.format("게임:채팅:%s:%s", nicks[index], 내용));
	}
	
	public void setId(int id) {
		this.id=id;
		this.setTitle(String.format("id:%d, 방제목:%s", this.id, this.name));
		this.write("마이룸:"+id);
		this.revalidate();
	}
	
	public int getId() { return this.id; }
	
	public int getMember() { return this.member; }

	//유저 셋팅
	//방에있는 유저들이 각 유저만큼 자리를 그릴 수있도록
	private void userSet() {
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<nicks.length; i++) {
			if(nicks[i]==null) break;
			builder.append(nicks[i]+",");
		}
		if(member>=4) writers[0].println("게임:방장준비");
		for(int i=0; i<member; i++) writers[i].println("게임:유저:"+member+"/"+i+"/"+builder.toString().substring(0,builder.length()-1));
	}
	
	public void addMember(BufferedReader reader, PrintWriter writer, String nick) {
		readers[member] = reader;
		writers[member] = writer;
		nicks[member] = nick;
		writer.println("방생성:"+this.id);
		
		this.member = (member+1)%(ROOM_MEMBER_MAX_NUM+1);
		
		this.titleBar.setText(String.format("방인원:%d/7, 방장:%s", member, nicks[방장]));
		this.revalidate();
		
		userSet();
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
		
		if(member==0) this.dispose();//멤버없으면 종료
		else {
			this.titleBar.setText(String.format("방인원:%d/7, 방장:%s", member, nicks[방장]));
			userSet();
		}
		this.revalidate();
	}
	
	public void write(String 내용) {
		for(int i=0; i<writers.length; i++) 
			if(writers[i]!=null) writers[i].println(내용);
	}
	
	@Override
	public String toString() { return this.id+"/"+this.name+"/"+Integer.toString(member); }
	
	//카드
	class Card{
		String name;
		String sign;
		int num;
		public Card(String name, String sign, int num) {
			this.name=name;
			this.sign=sign;
			this.num=num;
		}
		
		@Override
		public String toString() {
			return String.format("%s %d%s", name, num, sign);
		}
	}
}
