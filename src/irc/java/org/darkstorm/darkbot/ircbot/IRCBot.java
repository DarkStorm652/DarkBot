package org.darkstorm.darkbot.ircbot;

import org.darkstorm.darkbot.DarkBot;
import org.darkstorm.darkbot.bot.*;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.irc.dcc.DCCHandler;
import org.darkstorm.darkbot.ircbot.logging.*;
import org.darkstorm.darkbot.loopsystem.LoopManager;

@BotManifest(name = "IRCBot", botDataClass = IRCBotData.class)
public class IRCBot extends Bot {
	public static final int DEFAULT_PORT = 6667;

	private final LoopManager loopManager;
	private final EventHandler eventHandler;
	private final ServerHandler serverHandler;
	private final ConnectHandler connectHandler;
	private final CommandHandler commandHandler;
	private final MessageHandler messageHandler;
	private final ChannelHandler channelHandler;
	private final NicknameHandler nicknameHandler;
	private final PermissionsHandler permissionsHandler;
	private final DCCHandler dccHandler;

	private IRCLogger logger;

	public IRCBot(DarkBot darkBot, IRCBotData botData) {
		super(darkBot);
		if(!botData.isValid())
			throw new IllegalArgumentException("Invalid bot data");
		loopManager = new LoopManager(new ThreadGroup("IRCBot Loop Threads"));
		logger = new IRCSystemLogger(this);
		eventHandler = new EventHandler(this);
		serverHandler = new ServerHandler(this, botData);
		commandHandler = new CommandHandler(this);
		messageHandler = new MessageHandler(this);
		nicknameHandler = new NicknameHandler(this, botData);
		channelHandler = new ChannelHandler(this, botData);
		permissionsHandler = new PermissionsHandler(this, botData);
		connectHandler = new ConnectHandler(this);
		dccHandler = new DCCHandler(this);
		connect();
	}

	public boolean connect() {
		if(serverHandler.isConnected())
			return false;
		return connectHandler.connect();
	}

	public boolean disconnect() {
		if(!serverHandler.isConnected())
			return false;
		return connectHandler.disconnect();
	}

	@Override
	public boolean isConnected() {
		return serverHandler.isConnected();
	}

	public EventHandler getEventHandler() {
		return eventHandler;
	}

	public LoopManager getLoopManager() {
		return loopManager;
	}

	public ServerHandler getServerHandler() {
		return serverHandler;
	}

	public CommandHandler getCommandHandler() {
		return commandHandler;
	}

	public MessageHandler getMessageHandler() {
		return messageHandler;
	}

	public ChannelHandler getChannelHandler() {
		return channelHandler;
	}

	public NicknameHandler getNicknameHandler() {
		return nicknameHandler;
	}

	public PermissionsHandler getPermissionsHandler() {
		return permissionsHandler;
	}

	public DCCHandler getDCCHandler() {
		return dccHandler;
	}

	public IRCLogger getLogger() {
		return logger;
	}

	public String getQuitMessage() {
		return connectHandler.getQuitMessage();
	}

	public void setQuitMessage(String quitMessage) {
		connectHandler.setQuitMessage(quitMessage);
	}

	public void setLogger(IRCLogger logger) {
		this.logger = logger;
	}
}
