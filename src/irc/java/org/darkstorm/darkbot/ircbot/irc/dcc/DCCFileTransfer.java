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
import java.net.*;
import java.util.*;

import org.darkstorm.darkbot.ircbot.IRCBot;
import org.darkstorm.darkbot.ircbot.events.DCCEvent;
import org.darkstorm.darkbot.ircbot.util.Tools;

/**
 * This class is used to administer a DCC file transfer.
 * 
 * @since 1.2.0
 * @author Paul James Mutton, <a
 *         href="http://www.jibble.org/">http://www.jibble.org/</a>
 * @version 1.5.0 (Build time: Mon Dec 14 20:07:17 2009)
 */
public class DCCFileTransfer implements DCCTransfer {
	private IRCBot bot;
	private DCCHandler handler;
	private String nick;
	private String login = null;
	private String hostname = null;
	private String type;
	private long address;
	private int port;
	private long size;
	private boolean received;

	private Socket socket = null;
	private long progress = 0;
	private File file = null;
	private int timeout = 0;
	private boolean incoming;
	private long packetDelay = 0;

	private long startTime = 0;

	private List<Integer> ports;
	private Exception exception = null;

	/**
	 * The default buffer size to use when sending and receiving files.
	 */
	public static final int BUFFER_SIZE = 1024;

	/**
	 * Constructor used for receiving files.
	 */
	DCCFileTransfer(IRCBot bot, String nick, String login, String hostname,
			String type, String filename, long address, int port, long size) {
		this.bot = bot;
		handler = bot.getDCCHandler();
		this.nick = nick;
		this.login = login;
		this.hostname = hostname;
		this.type = type;
		file = new File(filename);
		this.address = address;
		this.port = port;
		this.size = size;
		received = false;

		incoming = true;
	}

	/**
	 * Constructor used for sending files.
	 */
	public DCCFileTransfer(IRCBot bot, File file, String nick, int timeout,
			int... ports) {
		this.bot = bot;
		handler = bot.getDCCHandler();
		this.nick = nick;
		this.file = file;
		size = file.length();
		this.timeout = timeout;
		received = true;
		List<Integer> portList;
		if(ports.length > 0) {
			portList = new ArrayList<Integer>();
			for(Integer port : ports)
				portList.add(port);
		} else
			portList = handler.getDCCPorts();
		this.ports = portList;

		incoming = false;
	}

	/**
	 * Receives a DccFileTransfer and writes it to the specified file. Resuming
	 * allows a partial download to be continue from the end of the current file
	 * contents.
	 * 
	 * @param file
	 *            The file to write to.
	 * @param resume
	 *            True if you wish to try and resume the download instead of
	 *            overwriting an existing file.
	 */
	public synchronized void receive(File file, boolean resume) {
		if(!received) {
			received = true;
			this.file = file;

			if(type.equals("SEND") && resume) {
				progress = file.length();
				if(progress == 0) {
					doReceive(file, false);
				} else {
					bot.getMessageHandler().sendCTCPMessage(nick,
							"DCC RESUME file.ext " + port + " " + progress);
					handler.addAwaitingResume(this);
				}
			} else {
				progress = file.length();
				doReceive(file, resume);
			}
		}
	}

	/**
	 * Receive the file in a new thread.
	 */
	void doReceive(final File file, final boolean resume) {
		new Thread() {
			@Override
			public void run() {

				BufferedOutputStream foutput = null;

				try {

					// Convert the integer address to a proper IP address.
					int[] ip = Tools.longToIp(address);
					String ipStr = ip[0] + "." + ip[1] + "." + ip[2] + "."
							+ ip[3];

					// Connect the socket and set a timeout.
					socket = new Socket(ipStr, port);
					socket.setSoTimeout(30 * 1000);
					startTime = System.currentTimeMillis();

					// No longer possible to resume this transfer once it's
					// underway.
					handler.removeAwaitingResume(DCCFileTransfer.this);

					BufferedInputStream input = new BufferedInputStream(
							socket.getInputStream());
					BufferedOutputStream output = new BufferedOutputStream(
							socket.getOutputStream());

					// Following line fixed for jdk 1.1 compatibility.
					foutput = new BufferedOutputStream(new FileOutputStream(
							file.getCanonicalPath(), resume));

					byte[] inBuffer = new byte[BUFFER_SIZE];
					byte[] outBuffer = new byte[4];
					int bytesRead = 0;
					while((bytesRead = input.read(inBuffer, 0, inBuffer.length)) != -1) {
						foutput.write(inBuffer, 0, bytesRead);
						progress += bytesRead;
						// Send back an acknowledgement of how many bytes we
						// have got so far.
						outBuffer[0] = (byte) ((progress >> 24) & 0xff);
						outBuffer[1] = (byte) ((progress >> 16) & 0xff);
						outBuffer[2] = (byte) ((progress >> 8) & 0xff);
						outBuffer[3] = (byte) ((progress >> 0) & 0xff);
						output.write(outBuffer);
						output.flush();
						delay();
					}
					foutput.flush();
				} catch(Exception e) {
					exception = e;
				} finally {
					try {
						foutput.close();
						socket.close();
					} catch(Exception anye) {
						// Do nothing.
					}
				}

				bot.getEventHandler().onFileTransferFinished(
						new DCCEvent(handler, DCCFileTransfer.this));
			}
		}.start();
	}

	/**
	 * Method to send the file inside a new thread.
	 */
	public void doSend(final boolean allowResume) {
		new Thread() {
			@Override
			public void run() {

				BufferedInputStream finput = null;

				try {

					ServerSocket ss = null;

					if(ports == null) {
						ss = new ServerSocket(0);
					} else {
						for(int port : ports) {
							try {
								ss = new ServerSocket(port);
								break;
							} catch(Exception e) {}
						}
						if(ss == null)
							throw new IOException("All ports given are in use.");
					}

					ss.setSoTimeout(timeout);
					port = ss.getLocalPort();
					InetAddress inetAddress = handler.getDCCInetAddress();
					if(inetAddress == null) {
						inetAddress = bot.getServerHandler().getConnection()
								.getSocket().getInetAddress();
					}
					byte[] ip = inetAddress.getAddress();
					long ipNum = Tools.ipToLong(ip);

					// Rename the filename so it has no whitespace in it when we
					// send it.
					// .... I really should do this a bit more nicely at some
					// point ....
					String safeFilename = file.getName().replace(' ', '_');
					safeFilename = safeFilename.replace('\t', '_');

					if(allowResume) {
						handler.addAwaitingResume(DCCFileTransfer.this);
					}

					// Send the message to the user, telling them where to
					// connect to in order to get the file.
					bot.getMessageHandler().sendCTCPMessage(
							nick,
							"DCC SEND " + safeFilename + " " + ipNum + " "
									+ port + " " + file.length());

					// The client may now connect to us and download the file.
					socket = ss.accept();
					socket.setSoTimeout(30000);
					startTime = System.currentTimeMillis();

					// No longer possible to resume this transfer once it's
					// underway
					if(allowResume)
						handler.removeAwaitingResume(DCCFileTransfer.this);

					// Might as well close the server socket now; it's finished
					// with.
					ss.close();

					BufferedOutputStream output = new BufferedOutputStream(
							socket.getOutputStream());
					BufferedInputStream input = new BufferedInputStream(
							socket.getInputStream());
					finput = new BufferedInputStream(new FileInputStream(file));

					// Check for resuming.
					if(progress > 0) {
						long bytesSkipped = 0;
						while(bytesSkipped < progress) {
							bytesSkipped += finput
									.skip(progress - bytesSkipped);
						}
					}

					byte[] outBuffer = new byte[BUFFER_SIZE];
					byte[] inBuffer = new byte[4];
					int bytesRead = 0;
					while((bytesRead = finput.read(outBuffer, 0,
							outBuffer.length)) != -1) {
						output.write(outBuffer, 0, bytesRead);
						output.flush();
						input.read(inBuffer, 0, inBuffer.length);
						progress += bytesRead;
						delay();
					}
				} catch(Exception e) {
					exception = e;
				} finally {
					try {
						finput.close();
						socket.close();
					} catch(Exception e) {
						// Do nothing.
					}
				}

				bot.getEventHandler().onFileTransferFinished(
						new DCCEvent(handler, DCCFileTransfer.this));
			}
		}.start();
	}

	/**
	 * Package mutator for setting the progress of the file transfer.
	 */
	void setProgress(long progress) {
		this.progress = progress;
	}

	/**
	 * Delay between packets.
	 */
	private void delay() {
		if(packetDelay > 0) {
			try {
				Thread.sleep(packetDelay);
			} catch(InterruptedException e) {
				// Do nothing.
			}
		}
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
	 * Returns the login of the file sender.
	 * 
	 * @return the login of the file sender. null if we are sending.
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Returns the hostname of the file sender.
	 * 
	 * @return the hostname of the file sender. null if we are sending.
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * Returns the suggested file to be used for this transfer.
	 * 
	 * @return the suggested file to be used.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Returns the port number to be used when making the connection.
	 * 
	 * @return the port number.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Returns true if the file transfer is incoming (somebody is sending the
	 * file to us).
	 * 
	 * @return true if the file transfer is incoming.
	 */
	public boolean isIncoming() {
		return incoming;
	}

	/**
	 * Returns true if the file transfer is outgoing (we are sending the file to
	 * someone).
	 * 
	 * @return true if the file transfer is outgoing.
	 */
	public boolean isOutgoing() {
		return !isIncoming();
	}

	/**
	 * Sets the delay time between sending or receiving each packet. Default is
	 * 0. This is useful for throttling the speed of file transfers to maintain
	 * a good quality of service for other things on the machine or network.
	 * 
	 * @param millis
	 *            The number of milliseconds to wait between packets.
	 */
	public void setPacketDelay(long millis) {
		packetDelay = millis;
	}

	/**
	 * returns the delay time between each packet that is send or received.
	 * 
	 * @return the delay between each packet.
	 */
	public long getPacketDelay() {
		return packetDelay;
	}

	/**
	 * Returns the size (in bytes) of the file being transfered.
	 * 
	 * @return the size of the file. Returns -1 if the sender did not specify
	 *         this value.
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Returns the progress (in bytes) of the current file transfer. When
	 * resuming, this represents the total number of bytes in the file, which
	 * may be greater than the amount of bytes resumed in just this transfer.
	 * 
	 * @return the progress of the transfer.
	 */
	public long getProgress() {
		return progress;
	}

	/**
	 * Returns the progress of the file transfer as a percentage. Note that this
	 * should never be negative, but could become greater than 100% if you
	 * attempt to resume a larger file onto a partially downloaded file that was
	 * smaller.
	 * 
	 * @return the progress of the transfer as a percentage.
	 */
	public double getProgressPercentage() {
		return 100 * (getProgress() / (double) getSize());
	}

	/**
	 * Stops the DCC file transfer by closing the connection.
	 */
	public void close() {
		try {
			socket.close();
		} catch(Exception e) {
			// Let the DCC manager worry about anything that may go wrong.
		}
	}

	/**
	 * Returns the rate of data transfer in bytes per second. This value is an
	 * estimate based on the number of bytes transfered since the connection was
	 * established.
	 * 
	 * @return data transfer rate in bytes per second.
	 */
	public long getTransferRate() {
		long time = (System.currentTimeMillis() - startTime) / 1000;
		if(time <= 0) {
			return 0;
		}
		return getProgress() / time;
	}

	public Exception getException() {
		return exception;
	}

	/**
	 * Returns the address of the sender as a long.
	 * 
	 * @return the address of the sender as a long.
	 */
	public long getNumericalAddress() {
		return address;
	}

}
