package org.darkstorm.darkbot.ircbot.irc;

import java.util.*;

import org.darkstorm.darkbot.ircbot.IRCBot;
import org.darkstorm.darkbot.ircbot.handlers.*;

public class Channel {
	private IRCBot bot;
	private ChannelHandler channelHandler;
	private String name;
	private List<User> users;

	public Channel(ChannelHandler channelHandler, String name, String topic) {
		testArguments(channelHandler, name);
		bot = channelHandler.getBot();
		this.channelHandler = channelHandler;
		this.name = name;
		users = new ArrayList<User>();
	}

	private void testArguments(ChannelHandler channelHandler, String name) {
		if(channelHandler == null || name == null)
			throw new NullPointerException();
		if(!isChannel(name))
			throw new IllegalArgumentException(
					"The channel name must start with # & ! or +");
	}

	public void join() {
		join(channelHandler.getJoinMessage());
	}

	public void join(String joinMessage) {
		MessageHandler messageHandler = bot.getMessageHandler();
		messageHandler.sendRaw("JOIN " + name);
		sendHello(joinMessage);
	}

	private void sendHello(String joinMessage) {
		if(channelHandler.getJoinMessage() != null) {
			MessageHandler messageHandler = bot.getMessageHandler();
			messageHandler.sendMessage(name, joinMessage);
		}
	}

	public void part() {
		part(channelHandler.getPartMessage());
	}

	public void part(String partMessage) {
		MessageHandler messageHandler = bot.getMessageHandler();
		String endPartMessage = " ";
		if(partMessage != null && partMessage.length() > 0) {
			if(partMessage.contains(" "))
				endPartMessage += ":";
			endPartMessage += partMessage;
		}
		messageHandler.sendRaw("PART " + name + endPartMessage);
	}

	public static boolean isChannel(String channel) {
		String channelPrefixes = "#&!+";
		for(char prefix : channelPrefixes.toCharArray())
			if(channel.startsWith(Character.toString(prefix)))
				return true;
		return false;
	}

	public ChannelHandler getChannelHandler() {
		return channelHandler;
	}

	public String getName() {
		return name;
	}

	public User[] getUsers() {
		synchronized(users) {
			return users.toArray(new User[users.size()]);
		}
	}

	public void setUsers(User[] users) {
		if(users == null)
			throw new IllegalArgumentException("param 0 (User) is null");
		synchronized(this.users) {
			this.users = Arrays.asList(users);
		}
	}
}
