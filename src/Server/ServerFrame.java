package Server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ServerFrame extends JFrame implements ActionListener{
	private static boolean status;
	private static JTextArea textArea;
	private static JButton button;
	private static Server server;
	
	public ServerFrame() {
		setTitle("Bang");//타이틀설정
		setSize(800,500);//크기설정
		setLocation(200,200);
		setResizable(false);//화면 사이즈 변경 불가
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//메인프레임을 닫아서 쓰레드가 실행됨을 방지하기위해 설정해줘야하는 메소드
		setLayout(null);//레이아웃 null 지정
		
		textArea = new JTextArea();//텍스트가 표기될 JTextArea
		textArea.setEditable(false);//수정불가
		JScrollPane scroll = new JScrollPane(textArea);//스크롤패널
		scroll.setBounds(25,25,750,350);//위치 크기지정
		
		button = new JButton("서버시작");
		button.setBounds(350, 390, 100, 50);
		button.addActionListener(this);
		
		add(scroll);
		add(button);
		
		setVisible(true);
		
		server = new Server();
		
	}
	
	protected static void textAppend(String text) {
		textArea.append(text+"\n");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(status) {//서버상태 확인
			textAppend("서버 중지");
			button.setText("서버 시작");
			status = false;
			server.serverClose();
		}else {
			textAppend("서버 시작");
			button.setText("서버 중지");
			status = true;
			server.serverOpen();
		}
	}
}
