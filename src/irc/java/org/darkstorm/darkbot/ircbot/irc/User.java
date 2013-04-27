package org.darkstorm.darkbot.ircbot.irc;

import org.darkstorm.darkbot.ircbot.IRCBot;
import org.darkstorm.darkbot.ircbot.handlers.*;

public class User {
	private IRCBot bot;
	private Channel channel;
	private String nickname;
	private String prefix;

	public User(Channel channel, String nickname, String prefix) {
		testArguments(channel, nickname, prefix);
		ChannelHandler channelHandler = channel.getChannelHandler();
		bot = channelHandler.getBot();
		this.channel = channel;
		this.nickname = nickname;
		this.prefix = prefix;
	}

	private void testArguments(Channel channel, String nickname, String prefix) {
		if(channel == null || nickname == null || prefix == null)
			throw new NullPointerException();
		if(nickname.length() < 1)
			throw new IllegalArgumentException("User must have a nickname!");
	}

	public void kick() {
		MessageHandler messageHandler = bot.getMessageHandler();
		messageHandler.sendRaw("KICK " + nickname);
	}

	public void op() {

	}

	public void deop() {

	}

	public void voice() {

	}

	public void devoice() {

	}

	public Channel getChannel() {
		return channel;
	}

	public String getNickname() {
		return nickname;
	}

	public String getPrefix() {
		return prefix;
	}

}
