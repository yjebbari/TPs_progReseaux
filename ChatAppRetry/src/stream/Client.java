package stream;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

import View.ChatRoomView;

/**
 * Client class allows the user to create a client to connect to the server of
 * the chat application.
 * 
 * @see Server
 * @see ClientManager
 * @see View.ChatRoomView
 * @author Nathalie Lebon and Yousra Jebbari
 *
 */
public class Client {

	private Socket clientSocket;
	private BufferedWriter socOut;
	private BufferedReader socIn;
	private String username;
	private ChatRoomView clientView;

	public String getUsername() {
		return username;
	}

	public BufferedWriter getSocOut() {
		return socOut;
	}

	/**
	 * Constructor of Client. Initializes the buffered reader an writer, the
	 * username and the socket of the client. Creates a ChatRoomView for the user to
	 * see and send messages.
	 * 
	 * @see View.ChatRoomView
	 * @param socket   is the client socket connects to the server.
	 * @param username is the username of the client.
	 */
	public Client(Socket socket, String username) {
		super();
		try {
			this.clientSocket = socket;
			this.username = username;

			this.socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			this.socOut = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

			clientView = new ChatRoomView(this);
		} catch (IOException e) {
			closeAll();
		}
	}

	/**
	 * Gets a <code>text</code> from the user interface that will be sent to the
	 * other clients.
	 * 
	 * @see View.ChatRoomView
	 * @param text is the String that will be sent to the other connected clients.
	 */
	public void sendMessage(String text) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			Date currentDate = new Date();

			socOut.write("(" + dateFormat.format(currentDate) + ") " + username + " : " + text);
			socOut.newLine();
			socOut.flush();
		} catch (IOException e) {
			closeAll();
		}
	}

	/**
	 * Creates a thread the read the incoming messages and displays them in the GUI.
	 * 
	 * @see View.ChatRoomView
	 */
	public void readMessages() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String textReceived;
				while (clientSocket.isConnected()) {
					try {
						textReceived = socIn.readLine();
						clientView.dispalyMessage(textReceived);
						clientView.dispalyMessage("\n");
					} catch (IOException e) {
						closeAll();
					}
				}
			}
		}).start();
	}

	/**
	 * In case of an exception or when the client disconnects, loses the buffered
	 * reader and writer and the socket of the client.
	 */
	public void closeAll() {
		try {
			if (socIn != null)
				socIn.close();
			if (socOut != null)
				socOut.close();
			if (clientSocket != null)
				clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Main method. Creates a client with a username chosen by the user in an Option
	 * pane, and starts reading the messages.
	 * 
	 * @see Client#readMessages()
	 * @param args are the execution parameters : the IP address of the server and
	 *             the port to connect the client.
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage: java EchoClient <EchoServer host> <EchoServer port>");
			System.exit(1);
		}

		try {
			String username = JOptionPane.showInputDialog("Enter your username");
			if (username != null) {
				Socket echoSocket = new Socket(args[0], new Integer(args[1]).intValue());

				Client client = new Client(echoSocket, username);

				client.getSocOut().write(username);
				client.getSocOut().newLine();
				client.getSocOut().flush();

				client.readMessages();
			}

		} catch (IOException e) {
			System.err.println("Couldn't get I/O for " + "the connection to:" + args[0]);
			System.exit(1);
		}
	}
}
