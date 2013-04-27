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

import java.net.InetAddress;
import java.util.*;

import org.darkstorm.darkbot.ircbot.IRCBot;
import org.darkstorm.darkbot.ircbot.events.DCCEvent;

/**
 * This class is used to process DCC events from the server.
 * 
 * @since 1.2.0
 * @author Paul James Mutton, <a
 *         href="http://www.jibble.org/">http://www.jibble.org/</a>
 * @version 1.5.0 (Build time: Mon Dec 14 20:07:17 2009)
 */
public class DCCHandler {
	private final IRCBot bot;
	private final Vector<DCCFileTransfer> awaitingResume = new Vector<DCCFileTransfer>();

	private List<Integer> dccPorts = null;
	private InetAddress dccInetAddress = null;

	/**
	 * Constructs a DccManager to look after all DCC SEND and CHAT events.
	 * 
	 * @param bot
	 *            The PircBot whose DCC events this class will handle.
	 */
	public DCCHandler(IRCBot bot) {
		this.bot = bot;
	}

	/**
	 * Processes a DCC request.
	 * 
	 * @return True if the type of request was handled successfully.
	 */
	public boolean processRequest(String nick, String login, String hostname,
			String request) {
		StringTokenizer tokenizer = new StringTokenizer(request);
		tokenizer.nextToken();
		String type = tokenizer.nextToken();
		String filename = tokenizer.nextToken();

		if(type.equals("SEND")) {
			long address = Long.parseLong(tokenizer.nextToken());
			int port = Integer.parseInt(tokenizer.nextToken());
			long size = -1;
			try {
				size = Long.parseLong(tokenizer.nextToken());
			} catch(Exception e) {
				// Stick with the old value.
			}

			DCCFileTransfer transfer = new DCCFileTransfer(bot, nick, login,
					hostname, type, filename, address, port, size);
			bot.getEventHandler().onIncomingFileTransfer(
					new DCCEvent(this, transfer));

		} else if(type.equals("RESUME")) {
			int port = Integer.parseInt(tokenizer.nextToken());
			long progress = Long.parseLong(tokenizer.nextToken());

			DCCFileTransfer transfer = null;
			synchronized(awaitingResume) {
				for(int i = 0; i < awaitingResume.size(); i++) {
					transfer = awaitingResume.elementAt(i);
					if(transfer.getNick().equals(nick)
							&& transfer.getPort() == port) {
						awaitingResume.removeElementAt(i);
						break;
					}
				}
			}

			if(transfer != null) {
				transfer.setProgress(progress);
				bot.getMessageHandler().sendCTCPNotice(nick,
						"DCC ACCEPT file.ext " + port + " " + progress);
			}

		} else if(type.equals("ACCEPT")) {
			int port = Integer.parseInt(tokenizer.nextToken());

			DCCFileTransfer transfer = null;
			synchronized(awaitingResume) {
				for(int i = 0; i < awaitingResume.size(); i++) {
					transfer = awaitingResume.elementAt(i);
					if(transfer.getNick().equals(nick)
							&& transfer.getPort() == port) {
						awaitingResume.removeElementAt(i);
						break;
					}
				}
			}

			if(transfer != null) {
				transfer.doReceive(transfer.getFile(), true);
			}

		} else if(type.equals("CHAT")) {
			long address = Long.parseLong(tokenizer.nextToken());
			int port = Integer.parseInt(tokenizer.nextToken());

			final DCCChat chat = new DCCChat(bot, nick, login, hostname,
					address, port);

			new Thread() {
				@Override
				public void run() {
					bot.getEventHandler().onIncomingChatRequest(
							new DCCEvent(DCCHandler.this, chat));
				}
			}.start();
		} else {
			return false;
		}

		return true;
	}

	/**
	 * Add this DccFileTransfer to the list of those awaiting possible resuming.
	 * 
	 * @param transfer
	 *            the DccFileTransfer that may be resumed.
	 */
	public void addAwaitingResume(DCCFileTransfer transfer) {
		synchronized(awaitingResume) {
			awaitingResume.addElement(transfer);
		}
	}

	/**
	 * Remove this transfer from the list of those awaiting resuming.
	 */
	public void removeAwaitingResume(DCCFileTransfer transfer) {
		awaitingResume.removeElement(transfer);
	}

	public InetAddress getDCCInetAddress() {
		return dccInetAddress;
	}

	public List<Integer> getDCCPorts() {
		return dccPorts;
	}
}
