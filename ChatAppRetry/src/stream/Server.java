/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package stream;

import java.io.*;
import java.net.*;

/**
 * Server creates a server for the chat application.
 * 
 * @see ClientManager
 * @see Client 
 * @author Nathalie Lebon and Yousra Jebbari
 *
 */
public class Server {

	/**
	 * main method : creates a server and creates a clientManager for each client
	 * connected.
	 * 
	 * @see Client
	 * @see ClientManager
	 * @param args are the execution parameters : here it is the server port to
	 *             which clients will connect.
	 **/
	public static void main(String args[]) {
		ServerSocket listenSocket = null;

		if (args.length != 1) {
			System.out.println("Usage: java EchoServer <EchoServer port>");
			System.exit(1);
		}
		try {
			listenSocket = new ServerSocket(Integer.parseInt(args[0])); // port
			System.out.println("Server ready...");

			while (!listenSocket.isClosed()) {
				Socket clientSocket = listenSocket.accept();
				System.out.println("Connexion from:" + clientSocket.getInetAddress());

				ClientManager clientManager = new ClientManager(clientSocket);
				Thread thread = new Thread(clientManager);
				thread.start();
			}
		} catch (Exception e) {
			try {
				if (listenSocket != null)
					listenSocket.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			System.err.println("Error in EchoServer:" + e);
		}
	}
}
