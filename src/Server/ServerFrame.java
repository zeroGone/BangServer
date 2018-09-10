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
		setTitle("Bang");//Ÿ��Ʋ����
		setSize(800,500);//ũ�⼳��
		setResizable(false);//ȭ�� ������ ���� �Ұ�
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//������������ �ݾƼ� �����尡 ������� �����ϱ����� ����������ϴ� �޼ҵ�
		setLayout(null);//���̾ƿ� null ����
		
		textArea = new JTextArea();//�ؽ�Ʈ�� ǥ��� JTextArea
		textArea.setEditable(false);//�����Ұ�
		JScrollPane scroll = new JScrollPane(textArea);//��ũ���г�
		scroll.setBounds(25,25,750,350);//��ġ ũ������
		
		JButton button = new JButton("��������");
		button.setBounds(350, 390, 100, 50);
		
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(status) {//�������� Ȯ��
					textAppend("���� ����");
					button.setText("���� ����");
					status = false;
				}else {
					textAppend("���� ����");
					button.setText("���� ����");
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
