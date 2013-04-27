package org.darkstorm.darkbot.ircbot.handlers;

import org.darkstorm.darkbot.ircbot.*;
import org.darkstorm.darkbot.ircbot.events.*;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.irc.parsing.LineParser.MessageType;

public class NicknameHandler extends IRCHandler implements MessageListener {
	private final String originalNickname;
	private String nickname;
	private String password;
	private boolean identify = false;

	public NicknameHandler(IRCBot bot, IRCBotData botInfo) {
		super(bot);
		originalNickname = botInfo.nickname;
		nickname = botInfo.nickname;
		password = botInfo.password;
		EventHandler eventHandler = bot.getEventHandler();
		eventHandler.addMessageListener(this);
	}

	public String getOriginalNickname() {
		return originalNickname;
	}

	public String getNickname() {
		return nickname;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String getName() {
		return "NicknameHandler";
	}

	public void identifyOnNickServMessage() {
		if(password != null && password.length() > 0)
			identify = true;
	}

	@Override
	public void onMessageReceived(MessageEvent event) {
		if(event.getMessage() instanceof UserMessage) {
			UserMessage message = (UserMessage) event.getMessage();
			String sender = message.getSender().getNickname();
			MessageType type = message.getType();
			if(identify && type.equals(MessageType.NOTICE)
					&& sender.equals("NickServ"))
				identify();
		} else if(event.getMessage() instanceof NickMessage) {
			NickMessage message = (NickMessage) event.getMessage();
			if(nickname.equals(message.getOriginalNickname())) {
				String newNickname = message.getNewNickname();
				NickServEvent nickServEvent = new NickServEvent(this, nickname,
						newNickname);
				nickname = newNickname;
				EventHandler eventHandler = bot.getEventHandler();
				eventHandler.onNicknameChanged(nickServEvent);
			}
		}
	}

	public void identify() {
		if(password == null)
			return;
		MessageHandler messageHandler = bot.getMessageHandler();
		messageHandler.sendMessage("NickServ", "identify " + password);
		NickServEvent nickServEvent = new NickServEvent(this, password);
		EventHandler eventHandler = bot.getEventHandler();
		eventHandler.onIdentified(nickServEvent);
		identify = false;
	}

	@Override
	public void onMessageSent(MessageEvent event) {
	}

	@Override
	public void onRawSent(MessageEvent event) {
	}

	public void setNickname(String nickname) {
		if(nickname == null)
			throw new NullPointerException();
		this.nickname = nickname;
		updateNickname();
	}

	public void updateNickname() {
		MessageHandler messageHandler = bot.getMessageHandler();
		messageHandler.sendRaw("NICK " + nickname);
	}

	@Override
	public void onNoticeSent(MessageEvent event) {
	}

}
