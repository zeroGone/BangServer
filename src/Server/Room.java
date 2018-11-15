package Server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

public class Room extends JFrame{
	public final int ROOM_MEMBER_MAX_NUM = 7;//�ִ� �ο� ��
	private final int ���� = 0;

	private int member;//���� �ο� ��

	private JTextArea log;//�α�
	private JTextArea titleBar;//Ÿ��Ʋ��
	
	private int id;//�� ���̵�
	private String name;//�� �̸�
	
	BufferedReader[] readers;//�� ������� reader
	PrintWriter[] writers;//�� ������� writer
	String[] nicks;//�� ������� �г��ӵ�
	
	public void setId(int id) {
		this.id=id;
		this.setTitle(String.format("id:%d, ������:%s", this.id, this.name));
		this.write("���̷�:"+id);
		this.revalidate();
	}
	
	public int getId() { return this.id; }
	
	public int getMember() { return this.member; }
	
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
	public String toString() { return this.id+"/"+this.name+"/"+Integer.toString(member); }
}
