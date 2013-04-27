package org.darkstorm.darkbot.ircbot.commands.defaults;

import org.darkstorm.darkbot.ircbot.commands.IRCCommand;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.irc.parsing.LineParser.MessageType;
import org.darkstorm.darkbot.loopsystem.*;

public class PingCommand extends IRCCommand {
	private long lastPongTime = System.currentTimeMillis();
	private LoopHandler continualPingController;

	public PingCommand(CommandHandler commandHandler) {
		super(commandHandler);
		Loopable continualPingLoop = new Loopable() {
			@Override
			public int loop() throws InterruptedException {
				if((System.currentTimeMillis() - lastPongTime) > 90000) {
					MessageHandler messageHandler = bot.getMessageHandler();
					ServerHandler serverHandler = bot.getServerHandler();
					messageHandler
							.sendRaw("PING :" + serverHandler.getServer());
					lastPongTime = System.currentTimeMillis();
				}
				return 1000;
			}
		};
		LoopManager loopManager = bot.getLoopManager();
		continualPingController = loopManager.addLoopable(continualPingLoop);
	}

	@Override
	public void execute(Message message) {
		MessageType type = message.getType();
		if(type.equals(MessageType.PING)) {
			PingMessage pingMessage = (PingMessage) message;
			MessageHandler messageHandler = bot.getMessageHandler();
			ServerHandler serverHandler = bot.getServerHandler();
			String pongMessage = ":" + serverHandler.getServer();
			if(pingMessage.getMessage() != null)
				pongMessage = pingMessage.getMessage();
			messageHandler.sendRaw("PONG " + pongMessage);
			lastPongTime = System.currentTimeMillis();
		} else if(message instanceof UserMessage
				&& ((UserMessage) message).isCTCP()
				&& ((UserMessage) message).getMessage().startsWith("PING")) {
			UserMessage userMessage = (UserMessage) message;
			String messageText = userMessage.getMessage();
			MessageHandler messageHandler = bot.getMessageHandler();
			String pongMessage = "";
			if(messageText.startsWith("PING "))
				pongMessage = messageText.substring("PING".length());
			messageHandler.sendNotice(userMessage.getSender().getNickname(),
					"PING" + pongMessage);
		}
	}

	@Override
	public String getName() {
		return "PING/PONG";
	}

	@Override
	public String getDescription() {
		return "Responds to server pings. Cannot be disabled.";
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void setEnabled(boolean enabled) {
		if(!enabled)
			continualPingController.stop();
		else
			continualPingController.start();
		super.setEnabled(enabled);
	}
}
