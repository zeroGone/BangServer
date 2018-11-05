package Server;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class Room {
	int id;
	String name;
	int member;
	BufferedReader[] readers;
	PrintWriter[] writers;
	String[] nicks;
	
	public Room(int id, String title) {
		this.id = id;
		this.name = title;
		readers = new BufferedReader[7];
		writers = new PrintWriter[7];
		nicks = new String[7];
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getMember() {
		return this.member;
	}
	
	public void addMember(BufferedReader reader, PrintWriter writer, String nick) {
		readers[member] = reader;
		writers[member] = writer;
		nicks[member] = nick;
		writer.println("방생성:"+this.id);
		this.member = (member+1)%7;
		//멤버 강종할때 아직안함
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
