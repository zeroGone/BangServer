package Server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ServerFrame extends JFrame implements ActionListener, WindowListener{
	protected static JTextArea textArea;
	private static JButton button;
	private static ServerMain server;

	public ServerFrame() {
		setTitle("Bang");//타이틀설정
		setSize(800,500);//크기설정
		setLocation(200,200);
		setResizable(false);//화면 사이즈 변경 불가
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//메인프레임을 닫아서 쓰레드가 실행됨을 방지하기위해 설정해줘야하는 메소드
		setLayout(null);//레이아웃 null 지정
		addWindowListener(this);
		
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
		
		server = new ServerMain();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(ServerMain.status) {//서버상태 확인
			textArea.append("서버 중지\n");
			button.setText("서버 시작");
			ServerMain.status = false;
			server.serverClose();
		}else {
			textArea.append("서버 시작\n");
			button.setText("서버 중지");
			ServerMain.status = true;
			server.serverOpen();
		}
	}

	//프레임 관리 리스너
	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowClosing(WindowEvent e) {
		System.out.println("서버 종료");
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
