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
		setTitle("Bang");//Ÿ��Ʋ����
		setSize(800,500);//ũ�⼳��
		setLocation(200,200);
		setResizable(false);//ȭ�� ������ ���� �Ұ�
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//������������ �ݾƼ� �����尡 ������� �����ϱ����� ����������ϴ� �޼ҵ�
		setLayout(null);//���̾ƿ� null ����
		
		textArea = new JTextArea();//�ؽ�Ʈ�� ǥ��� JTextArea
		textArea.setEditable(false);//�����Ұ�
		JScrollPane scroll = new JScrollPane(textArea);//��ũ���г�
		scroll.setBounds(25,25,750,350);//��ġ ũ������
		
		button = new JButton("��������");
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
		if(status) {//�������� Ȯ��
			textAppend("���� ����");
			button.setText("���� ����");
			status = false;
			server.serverClose();
		}else {
			textAppend("���� ����");
			button.setText("���� ����");
			status = true;
			server.serverOpen();
		}
	}
}
