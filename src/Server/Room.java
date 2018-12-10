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
	public final int ROOM_MEMBER_MAX_NUM = 7;//�ִ� �ο� ��
	private final int ���� = 0;
	private int member;//���� �ο� ��
	private int id;//�� ���̵�
	private int[] userLife;//������ �������
	private int deckCount;
	private int turn;
	private JTextArea log;//�α�
	private JTextArea titleBar;//Ÿ��Ʋ��
	private String name;//�� �̸�
	protected BufferedReader[] readers;//�� ������� reader
	private PrintWriter[] writers;//�� ������� writer
	private String[] nicks;//�� ������� �г��ӵ�
	private String[] userCharacters;//������ ĳ���͵�
	private String[] userJob;//������ ������
	private ArrayList<Card> deck;
	private boolean gameState;//���� ���� ����
	private Card tombCard;
	
	public Room(int id, String title) {
		setTitle(String.format("id:%d, ������:%s", id, title));
		setSize(600,400);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);//�����ư ������ �ƹ��͵�����
		setLayout(null);
		
		//Ÿ��Ʋ�� ����
		titleBar = new JTextArea();//��� �� ǥ���� �ؽ�Ʈ���̸���
		titleBar.setEditable(false);
		titleBar.setBounds(10, 10, 575, 20);
		titleBar.setBorder(new BevelBorder(BevelBorder.RAISED));
		
		//�α� ����
		log = new JTextArea();//�ؽ�Ʈ�� ǥ��� JTextArea
		log.setEditable(false);//�����Ұ�
		JScrollPane scroll = new JScrollPane(log);//��ũ���г�
		scroll.setBounds(10,40,575,320);//��ġ ũ������
		
		add(titleBar);
		add(scroll);
		setVisible(true);
		
		this.id = id;
		this.name = title;
		readers = new BufferedReader[ROOM_MEMBER_MAX_NUM];
		writers = new PrintWriter[ROOM_MEMBER_MAX_NUM];
		nicks = new String[ROOM_MEMBER_MAX_NUM];
	}
	
	//���� ����
	public void gameSetting() {
		//����ĳ���� ����
		this.userCharacters = new String[this.ROOM_MEMBER_MAX_NUM];
		String[] characters = {
				"��Ű��ũ","����Ѷ�","��Ʈĳ�õ�","���Ļ�","����","�������Ŀ�Ʈ","������ų��","�õ���÷",
				"���׸���","������Ű��","��������","�ָ�����","Ķ����Ƽ�ڳ�","ŰƮĮ��","���ζ�̳���","�����׷�"
		};
		boolean[] check = new boolean[16];
		
		//�������� ����
		this.userJob = new String[this.ROOM_MEMBER_MAX_NUM];
		ArrayList<String> job = new ArrayList<String>();
		job.add("���Ȱ�");
		job.add("������");
		job.add("������");
		job.add("�����");
		if(member>=5) job.add("�ΰ�");
		if(member>=6) job.add("������");
		if(member==7) job.add("�ΰ�");
		Collections.shuffle(job);
		
		for(int i=0; readers[i]!=null&&i<7; i++) {
			userJob[i]=job.get(i);
			int random = (int)(Math.random()*16);
			while(check[random]) random = (int)(Math.random()*16);
			userCharacters[i]=characters[random];
			check[random] = true;
		}
		
		//�ɸ����� ����
		StringBuilder builder = new StringBuilder();
		for(int i=0; readers[i]!=null&&i<7; i++) builder.append(userCharacters[i]+",");
		for(int i=0; writers[i]!=null&&i<7; i++) 
			writers[i].println(String.format("����:�ɸ�����:%d/%d/%s", member, i, builder.toString().substring(0, builder.length()-1)));
		
		userLife = new int[]{-1,-1,-1,-1,-1,-1,-1};
		//�������� ����
		int ���Ȱ� = 0;
		for(int i=0; readers[i]!=null&&i<7; i++) {
			int life = 4;
			if(userCharacters[i].equals("�����׷�")||userCharacters[i].equals("���׸���")) life--;
			if(userJob[i].equals("���Ȱ�")) {
				���Ȱ� = i;
				life++;
			}
			userLife[i]=life;
			writers[i].println("����:��������:"+userJob[i]);
		}
		
		for(int i=0; writers[i]!=null&&i<7; i++) {
			int distance = this.distanceCalculate(i, ���Ȱ�);
			writers[i].println(String.format("����:���Ȱ�����:%d", distance));//���Ȱ�����
		}

		builder.delete(0, builder.length());
		for(int i=0; userLife[i]!=-1&&i<7; i++) builder.append(userLife[i]+",");
		//������ ����
		for(int i=0; writers[i]!=null&&i<7; i++) 
			writers[i].println(String.format("����:������:%d:%d:%s",member,i,builder.toString().substring(0, builder.length()-1)));
		
		builder.delete(0, builder.length());
		for(int i=0; userLife[i]!=-1&&i<7; i++) {
			if(i==���Ȱ�)	builder.append((userLife[i]+2)+",");
			else builder.append(userLife[i]+",");
		}
		for(int i=0; writers[i]!=null&&i<7; i++) 
			writers[i].println(String.format("����:ī�弳��:%d:%d:%s",member,i,builder.toString().substring(0, builder.length()-1)));
		
    	//�� ���� ����
    	deck = new ArrayList<Card>();
    	for(int i=1; i<=13; i++) {
    		//��ī�� �߰�
    		deck.add(new Card("��","consume","���̾�",i));
    		if(i==1||i>=12) deck.add(new Card("��","consume","��Ʈ",i));
    		if(i>=2&&i<=9) deck.add(new Card("��","consume","Ŭ�ι�",i));
    		//������ �߰�
    		if(i>=2&&i<=8) deck.add(new Card("������","consume","��Ʈ",i));
    		if(i>=10||i==1) deck.add(new Card("������","consume","Ŭ�ι�", i));
    		//���� �߰�
    		if(i>=6&&i<=11) deck.add(new Card("����","consume", "��Ʈ", i));
    		if(i==1) {
    			deck.add(new Card("��","consume","�����̵�",i));//��ī�� �߰�
    			deck.add(new Card("ī��", "mount", "Ŭ�ι�", i));//ī�� �߰�
    			deck.add(new Card("���ذ�", "mount", "��Ʈ", i));//���ذ� �߰�
    		}else if(i==2) deck.add(new Card("���̳ʸ���Ʈ", "mount", "��Ʈ", i));//���̳ʸ���Ʈ �߰�
    		else if(i==4) deck.add(new Card("����", "mount", "��Ʈ", i));//���� �߰�
    		else if(i==5) deck.add(new Card("����","consume", "��Ʈ", i));//���� �߰�
    		else if(i==8) {
    			deck.add(new Card("����","consume", "Ŭ�ι�", i));//���� �߰�
    			deck.add(new Card("��Ż","consume", "���̾�", i));//��Ż�߰�
    			deck.add(new Card("��ü����", "mount", "��Ʈ", i));//��ü���� �߰�
    		}else if(i==9) {
    			//��ȭ�� �߰�
    			deck.add(new Card("��ȭ��", "consume","Ŭ�ι�", i));
    			//������ �߰�
    			deck.add(new Card("������", "consume","�����̵�", i));
    			deck.add(new Card("������", "consume","�����̵�", i));
    		}else if(i==10) {
    			//����� �߰�
    			deck.add(new Card("�����", "consume","��Ʈ", i));
    			//��ĳ���߰�
    			deck.add(new Card("��ĳ��", "mount",  "Ŭ�ι�", i));
    			deck.add(new Card("��ĳ��", "mount",  "��Ʈ", i));
    		}else if(i==11) deck.add(new Card("����", "consume","�����̵�", i));//���� �߰�
    		else if(i==12) {
    			deck.add(new Card("����", "consume","���̾�", i));
    			deck.add(new Card("��ȭ��", "consume","�����̵�", i));//��ȭ�� �߰�
    		}else if(i==13) {
    			deck.add(new Card("Ĺ���ο�", "consume","��Ʈ", i));
    			deck.add(new Card("�����ʵ�", "mount",  "�����̵�", i));
    			deck.add(new Card("������", "mount", "Ŭ�ι�", i));//������ �߰�
    		}
    		//�ε�� �߰�
    		if(i==1||i==13) deck.add(new Card("�ε��","consume", "���̾�", i));
    		//��Ż �߰�
    		if(i==1||i==11||i==12) deck.add(new Card("��Ż", "consume","��Ʈ", i));
    		//Ĺ ���ο� �߰�
    		if(i>=9&&i<=11) deck.add(new Card("Ĺ���ο�", "consume","���̾�", i));
    		//�����İ����� �߰�
    		if(i==3) deck.add(new Card("�����İ�����", "consume","��Ʈ", i));
    		//�����ʵ� �߰�
    		if(i==11||i==12) deck.add(new Card("�����ʵ�", "mount",  "Ŭ�ι�", i));
    		//�߻��� �߰�
    		if(i==8||i==9) deck.add(new Card("�߻���", "mount",  "��Ʈ", i));
    		//���� �߰�
    		if(i==12||i==13) deck.add(new Card("����", "mount",  "�����̵�", i));
    		//���� �߰�
    		if(i==10||i==11) deck.add(new Card("����", "mount",  "�����̵�", i));
    	}//�� ���� ��
    	//����
    	Collections.shuffle(deck);
    	
    	//"��ī��:�̸� ��ȣ����,�̸� ��ȣ����,~"
    	for(int i=0; userLife[i]!=-1&&i<7; i++) {
    		builder.delete(0, builder.length());
    		for(int j=0; j<userLife[i]; j++) builder.append(deck.get(deckCount++).toString()+",");
    		if(i==���Ȱ�) builder.append(deck.get(deckCount++).toString()+","+deck.get(deckCount++).toString()+",");
    		writers[i].println("����:��ī��:"+builder.toString().substring(0,builder.length()-1));
    	}

    	//���Ӽ��� ��
    	this.write("�α�:������ �����մϴ�!");
    	this.write("�α�:���Ȱ����� ����");
    	
    	writers[���Ȱ�].println("����:����");
    	this.turn=���Ȱ�;
    	this.gameState = true;
	}
	
	public void tombSet(String[] data) {
		Card card = new Card(data[1],data[0],data[2],Integer.parseInt(data[3]));
		tombCard = card;
		this.write("����:��������:"+card);
	}
	
	private int distanceCalculate(int start, int goal) {
		int distance = 0;
		if(start<=goal) distance = goal - start;
		else distance = member + goal - start;
		return distance;
	}
	
	public void sendUserCardData(int caster, int goal, String cards) {
		this.writers[caster].println(String.format(
				"����:ī������::%d:%d:%s", caster, goal, cards));
	}

	public void cattleRow(int goal, String data) {
		if(data.length()==0) this.write("�α�:Ĺ���ο� ����!");
		else {
			String[] card = data.split("/");
			for(int i=0; writers[i]!=null&&i<7; i++) {
				int distance = this.distanceCalculate(i, goal);
				if(!card[0].equals("mount")) writers[i].println(String.format("����:ī�尳������:%d:%d", distance, -1));
				else writers[i].println(String.format("����:��������:����:%d:%s", distance, card[1]));
			}
			if(!card[0].equals("mount")) writers[goal].println(String.format("����:ī�����:%s", data));
		}
	}

	public void bang(int caster, int goal, String check) {
		for(int i=0; writers[i]!=null&&i<7; i++) {
			int distance1 = caster;
			int distance2 = goal;
			distance1 = this.distanceCalculate(i, distance1);
			distance2 = this.distanceCalculate(i, distance2);
			if(check.equals("true")) writers[i].println(String.format("����:ī�尳������:%d:%d", distance2, -1));
			else writers[i].println(String.format("����:���������:%d:%d", distance2, -1));
//			writers[i].println(String.format("����:�ִ�:��:%d:%d:%s", caster, goal, check));
		}
	}
	
	public void nextTurn() {
		turn = (turn + 1)%member;
		while(this.userLife[turn]==0) turn = (turn + 1)%member;
		
    	this.write("�α�:"+this.nicks[turn]+"�� ��!");
    	
    	for(int i=0; writers[i]!=null&&i<7; i++) {
			int distance = this.distanceCalculate(i, turn);
			writers[i].println(String.format("����:��ο�:%d:%d", distance, 2));
		}
    	
    	if(deckCount>=80) {
    		Collections.shuffle(deck);
    		deckCount=0;
    	}
    	this.writers[turn].println("����:��ī��:"+deck.get(deckCount++).toString()+","+deck.get(deckCount++).toString());
    	this.writers[turn].println("����:����");
	}
	
	public void userCardUse(BufferedReader user, String value, int goal, String... data) {
		int index = 0;
		for(int i=0; readers[i]!=null&&i<7; i++) if(readers[i].equals(user)) index = i;
		Card card = new Card(data[1],data[0],data[2],Integer.parseInt(data[3]));
		//
		if(value.equals("����")) {
			tombCard = card;
			this.write(String.format("�α�:%s���� %s ī�带 ���Ƚ��ϴ�.", this.nicks[index], card.name));
			this.cardPayAni(index);
			this.write("����:��������:"+card);
		}
		//
		else if(goal == -1){
			this.write(String.format("�α�:%s���� %s ī�带 ����Ͽ����ϴ�.", this.nicks[index], card.name));
			//����ī�� �� ���
			if(card.����.equals("mount")) {
				for(int i=0; writers[i]!=null&&i<7; i++) {
					int distance = this.distanceCalculate(i, index);
					writers[i].println(String.format("����:��������:�߰�:%d:%s", distance, card));
					writers[i].println(String.format("����:ī�尳������:%d:%d", distance, -1));
				}
			}else if(card.name.equals("�����İ�����")){
				if(deckCount>=80) {
		    		Collections.shuffle(deck);
		    		deckCount=0;
		    	}
				writers[index].println("����:��ī��:"+
						deck.get(deckCount++).toString()+","+deck.get(deckCount++).toString()+","+deck.get(deckCount++).toString());
				this.write("����:�ִ�:�����İ�����");
				for(int i=0; writers[i]!=null&&i<7; i++) {
					int distance = this.distanceCalculate(i, index);
					writers[i].println(String.format("����:ī�尳������:%d:%d", distance, 2));
				}
			}else if(card.name.equals("������")) {
				if(deckCount>=80) {
		    		Collections.shuffle(deck);
		    		deckCount=0;
		    	}
				writers[index].println("����:��ī��:"+
						deck.get(deckCount++).toString()+","+deck.get(deckCount++).toString());
				this.write("����:�ִ�:������");
				for(int i=0; writers[i]!=null&&i<7; i++) {
					int distance = this.distanceCalculate(i, index);
					writers[i].println(String.format("����:ī�尳������:%d:%d", distance, 1));
				}
			}
		}
		//
		else {
			this.write(String.format("�α�:%s���� %s�Կ��� %s ī�带 ����Ͽ����ϴ�.", this.nicks[index], this.nicks[(index+goal)%member], card.name));
			switch(data[1]){
				case "Ĺ���ο�":
					writers[(index+goal)%member].println(
							String.format("����:Ĺ���ο�::%d:%d:", index, (index+goal)%member));
					this.cardPayAni(index);
					break;
				case "��":
					writers[(index+goal)%member].println(
							String.format("����:��::%d:%d:", index, (index+goal)%member));
					this.cardPayAni(index);
					break;
			}
		}
		
		
	}

	private void cardPayAni(int index) {
		//�ִϸ��̼�
		for(int i=0; writers[i]!=null&&i<7; i++) {
			int distance = this.distanceCalculate(i, index);
			writers[i].println(String.format("����:ī�峿:%d", distance));
		}
	}

	//���� ä��
	public void chatting(BufferedReader reader, String ����) {
		int index =0; 
		for(int i=0; readers[i]!=null&&i<this.ROOM_MEMBER_MAX_NUM; i++) if(readers[i].equals(reader)) index = i;
		this.write(String.format("����:ä��:%s:%s", nicks[index], ����));
	}
	
	public void setId(int id) {
		this.id=id;
		this.setTitle(String.format("id:%d, ������:%s", this.id, this.name));
		this.write("���̷�:"+id);
		this.revalidate();
	}
	
	public int getId() { return this.id; }
	
	public int getMember() { return this.member; }

	public boolean getGameState() { return this.gameState; }
	
	//���� ����
	//�濡�ִ� �������� �� ������ŭ �ڸ��� �׸� ���ֵ���
	private void userSet() {
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<nicks.length; i++) {
			if(nicks[i]==null) break;
			builder.append(nicks[i]+",");
		}
		if(member>=4) writers[0].println("����:�����غ�");
		for(int i=0; i<member; i++) writers[i].println("����:����:"+member+"/"+i+"/"+builder.toString().substring(0,builder.length()-1));
	}
	
	public void addMember(BufferedReader reader, PrintWriter writer, String nick) {
		readers[member] = reader;
		writers[member] = writer;
		nicks[member] = nick;
		writer.println("�����:"+this.id);
		
		this.member = (member+1)%(ROOM_MEMBER_MAX_NUM+1);
		
		this.titleBar.setText(String.format("���ο�:%d/7, ����:%s", member, nicks[����]));
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
		
		if(member==0) this.dispose();//��������� ����
		else {
			this.titleBar.setText(String.format("���ο�:%d/7, ����:%s", member, nicks[����]));
			userSet();
		}
		this.revalidate();
	}
	
	public void write(String ����) {
		for(int i=0; i<writers.length; i++) 
			if(writers[i]!=null) writers[i].println(����);
	}
	
	@Override
	public String toString() { 
		String str;
		if(this.gameState) str = "Playing";
		else str = "Waiting";
		
		return this.id+"/"+this.name+"/"+str+"/"+Integer.toString(member); }
	
	//ī��
	class Card{
		String name;
		String ����;
		String sign;
		int num;
		public Card(String name, String ����, String sign, int num) {
			this.name=name;
			this.����=����;
			this.sign=sign;
			this.num=num;
		}
		
		@Override
		public String toString() {
			return String.format("%s/%s/%s/%d", ����, name, sign, num);
		}
	}
}
