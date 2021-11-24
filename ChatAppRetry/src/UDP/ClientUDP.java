package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import View.ChatRoomUDPView;

/**
 * This class creates a UDP client.
 * 
 * @see View.ChatRoomUDPView
 * @author Nathalie Lebon and Yousra Jebbari
 *
 */
public class ClientUDP extends DatagramSocket {

	private MulticastSocket socket;
	private InetAddress group;
	private String username;
	private ChatRoomUDPView chatRoomView;

	public String getUsername() {
		return username;
	}

	/**
	 * Constructor of ClientUDP : initializes the username that is typed in by the
	 * user, the group and the socket of the client. Create a new instance
	 * ChatRoomUDPView.
	 * 
	 * @see View.ChatRoomUDPView
	 * @throws SocketException
	 */
	public ClientUDP() throws SocketException {
		super();
		try {
			this.username = JOptionPane.showInputDialog("Enter your username");
			if(this.username != null) {
				this.chatRoomView = new ChatRoomUDPView(this);
				this.group = InetAddress.getByName("228.5.6.7");
				this.socket = new MulticastSocket(1234);
				this.socket.joinGroup(group);
			} else
				System.exit(1);
			
		} catch (UnknownHostException e) {
			closeAll();
		} catch (IOException e) {
			closeAll();
		}

	}

	/**
	 * Takes the input of the user (String) from the GUI and sends it to the other
	 * clients of the multicast socket.
	 * 
	 * @param text is the text to be sent to the other clients.
	 */
	public void sendMessage(String text) {
		String textToSend;
		try {

			textToSend = username + " : " + text;
			DatagramPacket message = new DatagramPacket(textToSend.getBytes(), textToSend.length(), group, 1234);
			socket.send(message);

		} catch (IOException e) {
			closeAll();
		}
	}

	/**
	 * Creates a new thread to receive messages and print them to the user in the
	 * ChatRoomUDPView window.
	 */
	public void receiveMessage() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					byte[] buf = new byte[1000];
					DatagramPacket recv = new DatagramPacket(buf, buf.length);
					while (true) {
						socket.receive(recv);

						String text = new String(recv.getData(), 0, recv.getLength());
						chatRoomView.dispalyMessage(text);
					}
				} catch (IOException e) {
					closeAll();
				}

			}
		}).start();
	}

	/**
	 * The socket leaves the group.
	 */
	public void leaveGroup() {
		try {
			socket.leaveGroup(group);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes the multicast socket of the client after leaving the group.
	 */
	public void closeAll() {
		leaveGroup();
		if (socket != null)
			socket.close();
		System.exit(1);
	}

	/**
	 * Main method. Creates a ClientUDP and starts receiving and sending messages.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ClientUDP client = new ClientUDP();
			client.receiveMessage();
		} catch (SocketException e) {
			e.printStackTrace();
		}

	}

}
