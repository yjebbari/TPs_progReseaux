package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.LineBorder;

import UDP.ClientUDP;

/**
 * Is the user interface allowing to send messages to the other clients and
 * reading the messages they sent.
 * 
 * @see UDP.ClientUDP
 * 
 * @author Nathalie Lebon and Yousra Jebbari
 *
 */
public class ChatRoomUDPView extends JFrame {

	private static final long serialVersionUID = 1L;
	private ClientUDP client;
	private JTextArea messagesArea;
	private String messageToSend;

	public ClientUDP getClient() {
		return this.client;
	}

	/**
	 * Constructor of ChatRoomUDPView is a frame with a username and disconnect button
	 * area, a message area for the history, sent and received messages and a
	 * writing area with a send button for the client to write and send a new
	 * message.
	 * 
	 * @param client is the client to which the interface corresponds to; the window
	 *               is linked to the client.
	 */
	public ChatRoomUDPView(ClientUDP client) {
		super("Chat room");

		this.client = client;

		Color darkBlue = new Color(39, 38, 67);
		Color mediumDarkBlue = new Color(44, 105, 141);
		Color lightBlue = new Color(227, 246, 245);
		Color mediumLightBlue = new Color(186, 232, 232);

		this.setSize(700, 900);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		messagesArea = new JTextArea();
		messagesArea.setEditable(false);
		messagesArea.setBackground(mediumDarkBlue);
		messagesArea.setForeground(Color.WHITE);
		messagesArea.setLineWrap(true);
		messagesArea.setFont(new Font("Verdana", Font.PLAIN, 20));
		messagesArea.setBorder(new LineBorder(darkBlue, 4, true));
		JScrollPane messagesScrollPane = new JScrollPane(messagesArea);
		messagesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(messagesScrollPane, BorderLayout.CENTER);

		JPanel topArea = new JPanel();
		topArea.setForeground(Color.BLACK);
		topArea.setBackground(mediumLightBlue);
		topArea.setBorder(new LineBorder(darkBlue, 4, true));

		JLabel usernameArea = new JLabel(this.client.getUsername());
		usernameArea.setFont(new Font("Lucida Handwriting", Font.PLAIN, 50));
		topArea.add(usernameArea, BorderLayout.LINE_START);
		add(topArea, BorderLayout.PAGE_START);

		JButton disconnectUser = new JButton("Disconnect");
		disconnectUser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getClient() != null) {
					System.exit(1);
				}
			}

		});
		topArea.add(disconnectUser, BorderLayout.LINE_END);

		JPanel newMessageArea = new JPanel();
		newMessageArea.setPreferredSize(new Dimension(680, 200));
		newMessageArea.setBackground(darkBlue);

		JTextArea newMessageTextArea = new JTextArea();
		newMessageTextArea.setFont(new Font("Verdana", Font.PLAIN, 20));
		newMessageTextArea.setBackground(lightBlue);
		newMessageTextArea.setLineWrap(true);
		JScrollPane newMessageScrollPane = new JScrollPane(newMessageTextArea);
		newMessageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		newMessageScrollPane.setPreferredSize(new Dimension(680, 150));
		newMessageScrollPane.setBorder(new LineBorder(darkBlue, 4, true));
		newMessageArea.add(newMessageScrollPane, BorderLayout.PAGE_END);

		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!newMessageTextArea.getText().isEmpty()) {
					messageToSend = newMessageTextArea.getText();
					client.sendMessage(messageToSend);
					newMessageTextArea.setText("");
				}

			}

		});
		newMessageArea.add(sendButton, BorderLayout.LINE_END);

		add(newMessageArea, BorderLayout.PAGE_END);

		setVisible(true);
	}

	/**
	 * Adds a text message in the message area.
	 * 
	 * @param message is the String that will be displayed in the message area.
	 */
	public void dispalyMessage(String message) {
		this.messagesArea.append(message + "\n");
	}

}
