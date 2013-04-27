package org.darkstorm.darkbot.ircbot.handlers;

import java.util.*;

import org.darkstorm.darkbot.ircbot.*;
import org.darkstorm.darkbot.ircbot.events.*;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.irc.parsing.LineParser.MessageType;
import org.darkstorm.darkbot.ircbot.util.Connection;

public class ServerHandler extends IRCHandler implements MessageListener {
	private Map<String, String> properties;
	private Connection connection;
	private String server;
	private int port;

	public ServerHandler(IRCBot bot, IRCBotData botInfo) {
		super(bot);
		server = botInfo.server;
		port = botInfo.port;
		connection = new Connection(server, port);
		properties = new HashMap<String, String>();
		EventHandler eventHandler = bot.getEventHandler();
		eventHandler.addMessageListener(this);
	}

	@Override
	public void onMessageReceived(MessageEvent event) {
		Message message = event.getMessage();
		if(message.getType() == MessageType.SERVER)
			handleServerResponse((ServerResponseMessage) message);
	}

	private void handleServerResponse(ServerResponseMessage message) {
		int responseCode = message.getResponseCode();
		if(responseCode == 005)
			parse005SupportedProperties(message);
	}

	private void parse005SupportedProperties(ServerResponseMessage message) {
		String[] extraInfo = message.getExtraInfo();
		synchronized(properties) {
			properties.clear();
			for(int i = 0; i < extraInfo.length - 1; i++) {
				String key = "", value = "";
				if(extraInfo[i].contains("=")) {
					key = extraInfo[i].split("\\=")[0];
					value = extraInfo[i].substring((key + "=").length());
				} else
					key = extraInfo[i];
				properties.put(key, value);
			}
		}
	}

	@Override
	public void onMessageSent(MessageEvent event) {
	}

	@Override
	public void onNoticeSent(MessageEvent event) {
	}

	@Override
	public void onRawSent(MessageEvent event) {
	}

	public String getServer() {
		return server;
	}

	public int getPort() {
		return port;
	}

	public void setServer(String server) {
		this.server = server;
		connection.setHost(server);
	}

	public void setPort(int port) {
		this.port = port;
		connection.setPort(port);
	}

	public boolean connect() {
		if(!connection.connect())
			return false;
		return true;
	}

	public boolean disconnect() {
		if(!connection.disconnect())
			return false;
		return true;
	}

	public boolean isConnected() {
		return connection.isConnected();
	}

	public Connection getConnection() {
		return connection;
	}

	public Map<String, String> getProperties() {
		return Collections.unmodifiableMap(properties);
	}

	@Override
	public String getName() {
		return "ServerHandler";
	}

}
