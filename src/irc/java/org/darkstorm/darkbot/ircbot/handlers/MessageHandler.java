package org.darkstorm.darkbot.ircbot.handlers;

import java.io.*;
import java.net.SocketException;

import org.darkstorm.darkbot.ircbot.IRCBot;
import org.darkstorm.darkbot.ircbot.events.MessageEvent;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.irc.parsing.*;
import org.darkstorm.darkbot.ircbot.irc.parsing.LineParser.MessageType;
import org.darkstorm.darkbot.ircbot.logging.IRCLogger.IRCLogType;
import org.darkstorm.darkbot.ircbot.util.Connection;
import org.darkstorm.darkbot.loopsystem.*;

public class MessageHandler extends IRCHandler implements Loopable {
	private LoopHandler controller;
	private LineParser lineParser;
	private boolean flushOnRawEnabled = false;
	private boolean floodControlEnabled = true;
	private boolean muteEnabled = false;
	private long lastMessageSent = 0;
	private int messageCount = 0;

	public MessageHandler(IRCBot bot) {
		super(bot);
		lineParser = new LineParser(this);
		LoopManager loopManager = bot.getLoopManager();
		controller = loopManager.addLoopable(this);
	}

	public void sendMessage(String target, String message) {
		if(muteEnabled || (floodControlEnabled && useFloodControl()))
			return;
		String raw = "PRIVMSG " + target + " :" + message;
		sendRaw(raw);

		boolean ctcp = false;
		if(message.startsWith("\u0001") && message.endsWith("\u0001")) {
			message = message.substring(1, message.length() - 1);
			ctcp = true;
		}

		NicknameHandler nicknameHandler = bot.getNicknameHandler();
		generateSendEvent(MessageEvent.MESSAGE_SENT_ID,
				new UserMessage(MessageType.NOTICE, raw, new UserInfo(
						nicknameHandler.getNickname(), null, null), target,
						message, ctcp));
	}

	public void sendNotice(String target, String message) {
		if(muteEnabled || (floodControlEnabled && useFloodControl()))
			return;
		String raw = "NOTICE " + target + " :" + message;
		sendRaw(raw);

		boolean ctcp = false;
		if(message.startsWith("\u0001") && message.endsWith("\u0001")) {
			message = message.substring(1, message.length() - 1);
			ctcp = true;
		}

		NicknameHandler nicknameHandler = bot.getNicknameHandler();
		generateSendEvent(MessageEvent.NOTICE_SENT_ID,
				new UserMessage(MessageType.NOTICE, raw, new UserInfo(
						nicknameHandler.getNickname(), null, null), target,
						message, ctcp));
	}

	public void sendCTCPMessage(String target, String message) {
		sendMessage(target, "\u0001" + message + "\u0001");
	}

	public void sendCTCPNotice(String target, String message) {
		sendNotice(target, "\u0001" + message + "\u0001");
	}

	private boolean useFloodControl() {
		if((System.currentTimeMillis() - lastMessageSent) > 2000)
			messageCount = 1;
		else if(messageCount < 3)
			messageCount++;
		else
			return true;
		lastMessageSent = System.currentTimeMillis();
		return false;
	}

	public synchronized boolean sendRaw(String raw) {
		try {
			ServerHandler serverHandler = bot.getServerHandler();
			Connection connection = serverHandler.getConnection();
			BufferedWriter writer = connection.getOutputStreamWriter();
			writer.write(raw + "\n");
			if(flushOnRawEnabled)
				writer.flush();
			logger.log(this, IRCLogType.DEBUG, "<- " + raw);
			generateSendEvent(MessageEvent.RAW_MESSAGE_SENT_ID,
					new UnknownMessage(raw));
			return true;
		} catch(Exception exception) {
			logger.logException(this, IRCLogType.DEBUG_ERROR, exception);
			return false;
		}
	}

	public boolean flush() {
		try {
			ServerHandler serverHandler = bot.getServerHandler();
			Connection connection = serverHandler.getConnection();
			BufferedWriter writer = connection.getOutputStreamWriter();
			writer.flush();
			return true;
		} catch(Exception exception) {
			logger.logException(this, IRCLogType.DEBUG_ERROR, exception);
			return false;
		}
	}

	private void generateSendEvent(int id, Message message) {
		MessageEvent event = new MessageEvent(this, id, message);
		EventHandler eventHandler = (bot).getEventHandler();
		if(id == MessageEvent.MESSAGE_SENT_ID)
			eventHandler.onMessageSent(event);
		else if(id == MessageEvent.NOTICE_SENT_ID)
			eventHandler.onNoticeSent(event);
		else if(id == MessageEvent.RAW_MESSAGE_SENT_ID)
			eventHandler.onRawSent(event);
	}

	public int loop() {
		BufferedReader reader = createReader();
		if(reader == null)
			return Loopable.YIELD;
		while(bot.isConnected()) {
			readAndHandleLine(reader);
		}
		return Loopable.YIELD;
	}

	private BufferedReader createReader() {
		try {
			ServerHandler serverHandler = bot.getServerHandler();
			Connection connection = serverHandler.getConnection();
			return connection.getInputStreamReader();
		} catch(Exception exception) {
			logger.logException(this, IRCLogType.DEBUG_ERROR, exception);
			return null;
		}
	}

	private void readAndHandleLine(BufferedReader reader) {
		try {
			String inputLine = reader.readLine();
			if(inputLine != null) {
				logger.log(this, IRCLogType.DEBUG, "-> " + inputLine);
				try {
					Message message = lineParser.parse(inputLine);
					generateReceiveEvent(message);
				} catch(Exception exception) {
					logger.log(this, IRCLogType.DEBUG_ERROR,
							"Unable to parse line: " + inputLine);
					logger.logException(this, IRCLogType.DEBUG_ERROR, exception);
					return;
				}
			}
		} catch(Exception exception) {
			logger.logException(this, IRCLogType.DEBUG_ERROR, exception);
			if(exception instanceof SocketException)
				bot.disconnect();
		}
	}

	private void generateReceiveEvent(Message message) {
		MessageEvent event = new MessageEvent(this,
				MessageEvent.MESSAGE_RECEIVED_ID, message);
		EventHandler eventHandler = (bot).getEventHandler();
		eventHandler.onMessageReceived(event);
	}

	public LineParser getLineParser() {
		return lineParser;
	}

	public LoopHandler getController() {
		return controller;
	}

	public boolean isFloodControlEnabled() {
		return floodControlEnabled;
	}

	public boolean isFlushOnRawEnabled() {
		return flushOnRawEnabled;
	}

	public boolean isMuteEnabled() {
		return muteEnabled;
	}

	public void setFloodControlEnabled(boolean floodControlEnabled) {
		this.floodControlEnabled = floodControlEnabled;
	}

	public void setFlushOnRawEnabled(boolean flushOnRawEnabled) {
		this.flushOnRawEnabled = flushOnRawEnabled;
	}

	public void setMuteEnabled(boolean muteEnabled) {
		this.muteEnabled = muteEnabled;
	}

	@Override
	public String getName() {
		return "MessageHandler";
	}

}
