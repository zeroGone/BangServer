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

public class ServerFrame extends JFrame{
	private static boolean status;
	private static JTextArea textArea;
	
	public ServerFrame() {
		setTitle("Bang");//타이틀설정
		setSize(800,500);//크기설정
		setResizable(false);//화면 사이즈 변경 불가
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//메인프레임을 닫아서 쓰레드가 실행됨을 방지하기위해 설정해줘야하는 메소드
		setLayout(null);//레이아웃 null 지정
		
		textArea = new JTextArea();//텍스트가 표기될 JTextArea
		textArea.setEditable(false);//수정불가
		JScrollPane scroll = new JScrollPane(textArea);//스크롤패널
		scroll.setBounds(25,25,750,350);//위치 크기지정
		
		JButton button = new JButton("서버시작");
		button.setBounds(350, 390, 100, 50);
		
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(status) {//서버상태 확인
					textAppend("서버 중지");
					button.setText("서버 시작");
					status = false;
				}else {
					textAppend("서버 시작");
					button.setText("서버 중지");
					status = true;
					
					try {
						new Socket().connect(new InetSocketAddress("192.168.61.129",2018),2000);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					try {
//						new ServerThread().run();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
				}
			}
		});
		
		
		add(scroll);
		add(button);
		
		setVisible(true);
	}
	
	protected static void textAppend(String text) {
		textArea.append(text+"\n");
	}
}
