package Server;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class Room {
	String name;
	int member;
	BufferedReader[] readers;
	PrintWriter[] writers;
	
	public Room(String title, BufferedReader reader, PrintWriter writer) {
		name = title;
		readers = new BufferedReader[7];
		readers[0] = reader;
		writers = new PrintWriter[7];
		writers[0] = writer;
		member = 1;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getMember() {
		return this.member;
	}
	
	@Override
	public String toString() {
		return this.name+"/"+Integer.toString(member);
	}
}
