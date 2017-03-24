package GUI_talk;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.Point;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.text.PlainDocument;


public class GUI_talk2 extends Frame {
	private Button send ;
	private Button log  ;
	private Button clear ;
	private Button shake ;
	private TextField MyIP ;
	private TextArea writeArea ;
	private TextArea viewArea ;
	private TextField TextIP ;
	private DatagramSocket socket ;
	private FileInputStream check ;
	private FileOutputStream save ;
	InetAddress ip ;
	public GUI_talk2() throws Exception {
		initial() ;
		southPanel() ;
		midPanel() ;
		event() ;
	}
	private void initial() throws Exception {
		this.setLayout(new BorderLayout());
		this.setSize(650, 450);
		this.setTitle("神聊");
		this.setLocation(430,130);
		this.setVisible(true);
		new Receive().start();
		socket = new DatagramSocket() ;
		save = new FileOutputStream(new File("record.txt"), true) ;
	}
	public void goSend() throws Exception {
//		ip = InetAddress.getByName(TextIP.getText().toString());
		String ips = TextIP.getText().toString() ;
		ips = ips.trim().length() == 0 ? "所有人" : ips ;
		String SendText = writeArea.getText(); 
		byte[] mesg = SendText.getBytes() ;
		goSend(mesg);
		String Rec = getCurrentTime() + "  发送给  " + ips + "\r\n" + SendText + "\r\n" ;
		viewArea.append(Rec);
		writeArea.setText("");
		save(Rec) ;
	}
	private void goSend(byte[] mesg) throws IOException {
		ip = InetAddress.getByName(TextIP.getText().toString());
		DatagramPacket packet = 
				new DatagramPacket(mesg, mesg.length, ip, 8520);
		socket.send(packet);
	}
	private void event() {
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					socket.close();
//					check.close();
					save.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
		});
		send.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				try {
					goSend() ;
				} catch (Exception e1) {}
			}
		});
		log.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					check() ;
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clear() ;
			}
		});
		shake.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					goSend(new byte[] {-1});
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		writeArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						goSend() ;
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}
	private void midPanel() {
		Panel midP = new Panel() ;
		midP.setLayout(new BorderLayout());
		viewArea = new TextArea(3,1) ;
		writeArea = new TextArea(5,1) ;
		viewArea.setBackground(Color.WHITE);
		viewArea.setEditable(false);
		writeArea.setFont(new Font("1", Font.PLAIN, 15));
		viewArea.setFont(new Font("1", Font.PLAIN, 15));
		midP.add(writeArea, BorderLayout.SOUTH);
		midP.add(viewArea, BorderLayout.CENTER);
		this.add(midP, BorderLayout.CENTER) ;
		this.setSize(650, 455);
		
	}
	private void southPanel() throws UnknownHostException {
		TextField tf = new TextField(8) ;
		tf.setText("对方IP");
		Panel southP = new Panel();
		TextIP = new TextField(10) ;
		MyIP = new TextField(23) ;
		tf.setEditable(false);
		MyIP.setEditable(false);
		TextIP.setText("127.0.0.1");
		InetAddress addr = InetAddress.getLocalHost() ;
		MyIP.setText(addr.toString());
		send = new Button("发送") ;
		log = new Button("记录") ;
		clear = new Button("清屏") ;
		shake = new Button("震动") ;
		southP.add(tf);
		southP.add(TextIP) ;
		southP.add(send) ;
		southP.add(log) ;
		southP.add(clear) ;
		southP.add(shake) ;
		southP.add(MyIP) ;
		this.add(southP, BorderLayout.SOUTH) ;
	}
	public String getCurrentTime() {
		Date d = new Date(System.currentTimeMillis()) ;
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日hh:mm:ss") ;
		String time = sdf.format(d) ;
		return time;
	}
	public void save(String s) throws IOException {
		save.write(s.getBytes());
	}
	public void check() throws Exception {
		check = new FileInputStream(new File("record.txt")) ;
		ByteArrayOutputStream baos = new ByteArrayOutputStream() ; 
		byte[] arr = new byte[1024] ;
		int len ;
		while((len = check.read(arr)) != -1) {
			baos.write(arr, 0, len);
		}
		String str = baos.toString() ;
		viewArea.setText(str);
	}
	public void clear() {
		viewArea.setText("");
	}
	public void shake() throws InterruptedException {
		Point p = this.getLocation() ;
		for(int i = 0; i < 20; i++) {
			p.translate((int)(20*(Math.pow(-1, i))), 0);
			this.setLocation(p);
			Thread.sleep(50);
		}
		for(int i = 0; i < 20; i++) {
			p.translate(0, (int)(20*(Math.pow(-1, i+1))));
			this.setLocation(p);
			Thread.sleep(50);
		}
	}
	public class Receive extends Thread {
		public void run() {
			try {
				DatagramSocket socket = new DatagramSocket(8520) ;
				DatagramPacket packet = new DatagramPacket(new byte[1024], 1024) ;
				while(true) {
				socket.receive(packet);
					byte[] arr = packet.getData() ;
					int len = packet.getLength() ;
				if(arr[0] == -1 && len == 1) {
					shake() ;
					continue ;
				}
					String s = new String(arr, 0, len) ;
					ip = InetAddress.getByName(TextIP.getText().toString());
					Thread.sleep(500);
					String Rec = getCurrentTime() + "  收到来自  " + ip + "\r\n" + s + "\r\n" ;
					viewArea.append(Rec);
					save(Rec) ;
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) throws Exception {
		new GUI_talk2();
	}

}
