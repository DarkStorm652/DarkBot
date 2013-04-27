package org.darkstorm.darkbot.ircbot.handlers;

import org.darkstorm.darkbot.DarkBot;
import org.darkstorm.darkbot.ircbot.IRCBot;

public class ConnectHandler extends IRCHandler {
	private String quitMessage = null;

	public ConnectHandler(IRCBot bot) {
		super(bot);
	}

	public synchronized boolean connect() {
		ServerHandler serverHandler = bot.getServerHandler();
		if(serverHandler.isConnected() || !serverHandler.connect())
			return false;
		NicknameHandler nicknameHandler = bot.getNicknameHandler();
		nicknameHandler.updateNickname();
		MessageHandler messageHandler = bot.getMessageHandler();
		messageHandler.sendRaw("USER DarkBot * * :DarkBot " + DarkBot.VERSION);
		messageHandler.sendRaw("MODE +B");
		messageHandler.flush();
		messageHandler.setFlushOnRawEnabled(true);
		nicknameHandler.identifyOnNickServMessage();
		return true;
	}

	public synchronized boolean disconnect() {
		String endQuitMessage = "";
		if(quitMessage != null && quitMessage.length() > 0) {
			endQuitMessage += " ";
			if(containsNonletters(quitMessage))
				endQuitMessage += ":";
			endQuitMessage += quitMessage;
		}
		MessageHandler messageHandler = bot.getMessageHandler();
		messageHandler.sendRaw("QUIT" + endQuitMessage);
		messageHandler.flush();
		ServerHandler serverHandler = bot.getServerHandler();
		if(!serverHandler.isConnected())
			return false;
		return serverHandler.disconnect();
	}

	private boolean containsNonletters(String string) {
		for(char character : string.toCharArray())
			if(!Character.isLetter(character))
				return true;
		return false;
	}

	@Override
	public String getName() {
		return "ConnectHandler";
	}

	public synchronized String getQuitMessage() {
		return quitMessage;
	}

	public synchronized void setQuitMessage(String quitMessage) {
		this.quitMessage = quitMessage;
	}
}
