/* 
Copyright Paul James Mutton, 2001-2009, http://www.jibble.org/

This file is part of PircBot.

This software is dual-licensed, allowing you to choose between the GNU
General Public License (GPL) and the www.jibble.org Commercial License.
Since the GPL may be too restrictive for use in a proprietary application,
a commercial license is also provided. Full license information can be
found at http://www.jibble.org/licenses/

 */

package org.darkstorm.darkbot.ircbot.irc.dcc;

import java.io.*;
import java.net.Socket;

import org.darkstorm.darkbot.ircbot.IRCBot;
import org.darkstorm.darkbot.ircbot.util.Tools;

/**
 * This class is used to allow the bot to interact with a DCC Chat session.
 * 
 * @since 0.9c
 * @author Paul James Mutton, <a
 *         href="http://www.jibble.org/">http://www.jibble.org/</a>
 * @version 1.5.0 (Build time: Mon Dec 14 20:07:17 2009)
 */
public class DCCChat implements DCCTransfer {
	private IRCBot bot;
	private String nick;
	private String login = null;
	private String hostname = null;
	private BufferedReader reader;
	private BufferedWriter writer;
	private Socket socket;
	private boolean acceptable;
	private long address = 0;
	private int port = 0;

	/**
	 * This constructor is used when we are accepting a DCC CHAT request from
	 * somebody. It attempts to connect to the client that issued the request.
	 * 
	 * @param bot
	 *            An instance of the underlying PircBot.
	 * @param sourceNick
	 *            The nick of the sender.
	 * @param address
	 *            The address to connect to.
	 * @param port
	 *            The port number to connect to.
	 * @throws IOException
	 *             If the connection cannot be made.
	 */
	DCCChat(IRCBot bot, String nick, String login, String hostname,
			long address, int port) {
		this.bot = bot;
		this.address = address;
		this.port = port;
		this.nick = nick;
		this.login = login;
		this.hostname = hostname;
		acceptable = true;
	}

	/**
	 * This constructor is used after we have issued a DCC CHAT request to
	 * somebody. If the client accepts the chat request, then the socket we
	 * obtain is passed to this constructor.
	 * 
	 * @param bot
	 *            An instance of the underlying PircBot.
	 * @param sourceNick
	 *            The nick of the user we are sending the request to.
	 * @param socket
	 *            The socket which will be used for the DCC CHAT session.
	 * @throws IOException
	 *             If the socket cannot be read from.
	 */
	DCCChat(IRCBot bot, String nick, Socket socket) throws IOException {
		this.bot = bot;
		this.nick = nick;
		this.socket = socket;
		reader = new BufferedReader(new InputStreamReader(
				this.socket.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter(
				this.socket.getOutputStream()));
		acceptable = false;
	}

	/**
	 * Accept this DccChat connection.
	 * 
	 * @since 1.2.0
	 */
	public synchronized void accept() throws IOException {
		if(acceptable) {
			acceptable = false;
			int[] ip = Tools.longToIp(address);
			String ipStr = ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3];
			socket = new Socket(ipStr, port);
			reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));
		}
	}

	/**
	 * Reads the next line of text from the client at the other end of our DCC
	 * Chat connection. This method blocks until something can be returned. If
	 * the connection has closed, null is returned.
	 * 
	 * @return The next line of text from the client. Returns null if the
	 *         connection has closed normally.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public String readLine() throws IOException {
		if(acceptable) {
			throw new IOException(
					"You must call the accept() method of the DccChat request before you can use it.");
		}
		return reader.readLine();
	}

	/**
	 * Sends a line of text to the client at the other end of our DCC Chat
	 * connection.
	 * 
	 * @param line
	 *            The line of text to be sent. This should not include linefeed
	 *            characters.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public void sendLine(String line) throws IOException {
		if(acceptable) {
			throw new IOException(
					"You must call the accept() method of the DccChat request before you can use it.");
		}
		// No need for synchronization here really...
		writer.write(line + "\r\n");
		writer.flush();
	}

	/**
	 * Closes the DCC Chat connection.
	 * 
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public void close() throws IOException {
		if(acceptable) {
			throw new IOException(
					"You must call the accept() method of the DccChat request before you can use it.");
		}
		socket.close();
	}

	/**
	 * Returns the nick of the other user taking part in this file transfer.
	 * 
	 * @return the nick of the other user.
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * Returns the login of the DCC Chat initiator.
	 * 
	 * @return the login of the DCC Chat initiator. null if we sent it.
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Returns the hostname of the DCC Chat initiator.
	 * 
	 * @return the hostname of the DCC Chat initiator. null if we sent it.
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * Returns the BufferedReader used by this DCC Chat.
	 * 
	 * @return the BufferedReader used by this DCC Chat.
	 */
	public BufferedReader getBufferedReader() {
		return reader;
	}

	/**
	 * Returns the BufferedReader used by this DCC Chat.
	 * 
	 * @return the BufferedReader used by this DCC Chat.
	 */
	public BufferedWriter getBufferedWriter() {
		return writer;
	}

	/**
	 * Returns the raw Socket used by this DCC Chat.
	 * 
	 * @return the raw Socket used by this DCC Chat.
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * Returns the address of the sender as a long.
	 * 
	 * @return the address of the sender as a long.
	 */
	public long getNumericalAddress() {
		return address;
	}

	public IRCBot getBot() {
		return bot;
	}
}
