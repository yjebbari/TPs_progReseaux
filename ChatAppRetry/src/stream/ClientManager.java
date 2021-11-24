package stream;

import java.io.*;
import java.net.*;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * ClientManager handles a client and sends its messages to all other connected
 * clients Implements runnable to be executed in a thread.
 * 
 * @see Client
 * @see Server
 * @author Nathalie Lebon and Yousra Jebbari
 *
 */
public class ClientManager implements Runnable {

	private static ArrayList<ClientManager> clientManagers = new ArrayList<>();
	private Socket clientSocket;
	private BufferedWriter socOut;
	private BufferedReader socIn;
	private String clientUsername;
	private String messageHistory;

	public String getClientUsername() {
		return clientUsername;
	}

	public BufferedWriter getSocOut() {
		return socOut;
	}

	/**
	 * Constructor of ClientManager. Initializes the buffered reader and writer, the
	 * socket of the client, its username, and the path to the message history.
	 * Loads the message history and notifies the other connected clients of the new
	 * connection.
	 * 
	 * @see ClientManager#sendMessage(String)
	 * @see ClientManager#loadTextHistory()
	 * @see ClientManager#closeAll(Socket, BufferedReader, BufferedWriter)
	 * @see Client
	 * 
	 * @param clientSocket is the client socket that connects the client.
	 */
	public ClientManager(Socket clientSocket) {
		super();
		try {
			this.clientSocket = clientSocket;
			this.socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			this.socOut = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

			this.clientUsername = socIn.readLine();

			clientManagers.add(this);
			messageHistory = Paths.get("").toAbsolutePath().getParent().toString();
			messageHistory = messageHistory.replace(System.getProperty("file.separator"), "/");

			messageHistory += "/history/messageHistory.txt";
			loadTextHistory();

			sendMessage(clientUsername + " has entered the chat!");
		} catch (IOException e) {
			closeAll(clientSocket, socIn, socOut);
		}
	}

	/**
	 * Waits for a message to be sent by a client to send it to the other connected
	 * clients.
	 * 
	 * @see ClientManager#sendMessage(String)
	 * @see ClientManager#closeAll(Socket, BufferedReader, BufferedWriter)
	 * @see Client
	 */
	public void run() {
		String text;

		while (clientSocket.isConnected()) {
			try {
				text = socIn.readLine();
				sendMessage(text);
			} catch (IOException e) {
				closeAll(clientSocket, socIn, socOut);
				break;
			}
		}
	}

	/**
	 * Sends the parameter <code>text</code> to the other connected clients after
	 * saving it in the message history.
	 * 
	 * @see Client
	 * @see ClientManager#closeAll(Socket, BufferedReader, BufferedWriter)
	 * @param text is the String that will be sent to all clients except for the
	 *             client that sent it.
	 */
	public void sendMessage(String text) {
		try {
			PrintWriter fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(messageHistory, true)));
			fileWriter.println(text);
			fileWriter.close();
		} catch (IOException e) {
			System.out.println("Error in saving message.");
		}
		for (ClientManager clientManager : clientManagers) {
			try {
				if (!clientManager.getClientUsername().equals(this.clientUsername)) {
					clientManager.getSocOut().write(text);
					clientManager.getSocOut().newLine();
					clientManager.getSocOut().flush();
				}
			} catch (Exception e) {
				closeAll(clientSocket, socIn, socOut);
			}
		}
	}

	/**
	 * When a client disconnects, deletes the corresponding ClientManager from the
	 * client manager list and notifies the other connected clients.
	 * 
	 * @see ClientManager#sendMessage(String)
	 * @see Client
	 */
	public void deleteClientManager() {
		clientManagers.remove(this);
		sendMessage(this.clientUsername + " has left the chat");
	}

	/**
	 * In case of an exception or when a client disconnects, deletes the client
	 * manager, closes the buffered reader and writer and the socket of the client.
	 * 
	 * @see ClientManager#deleteClientManager()
	 * @param socket is the client socket to be closed.
	 * @param socIn  is the BufferedReader of the client to be closed.
	 * @param socOut is the BufferedWriter of the client to be closed.
	 */
	public void closeAll(Socket socket, BufferedReader socIn, BufferedWriter socOut) {
		deleteClientManager();
		try {
			if (socIn != null)
				socIn.close();
			if (socOut != null)
				socOut.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads the message history from the file of path stored in
	 * <code>messageHistory</code>, and sends it to the client that just connected
	 * to the server.
	 * 
	 * @see ClientManager#sendMessage(String)
	 * @see ClientManager#closeAll(Socket, BufferedReader, BufferedWriter)
	 * @see Client
	 * 
	 */
	public void loadTextHistory() {
		BufferedReader fileReader;
		try {
			socOut.write("----- Previous messages -----");
			socOut.newLine();
			socOut.flush();
			fileReader = new BufferedReader(new FileReader(messageHistory));
			String line;
			line = fileReader.readLine();
			while (line != null) {
				socOut.write(line);
				socOut.newLine();
				socOut.flush();

				line = fileReader.readLine();
			}
			socOut.write("-----------------------------");
			socOut.newLine();
			socOut.flush();
			fileReader.close();
		} catch (IOException e) {
			try {
				socOut.write("Error in loading previous messages.\n-----------------------------");
				socOut.newLine();
				socOut.flush();
			} catch (Exception exc) {
				closeAll(clientSocket, socIn, socOut);
			}
		}
	}
}
