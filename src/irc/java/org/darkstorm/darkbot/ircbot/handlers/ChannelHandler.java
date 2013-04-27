package org.darkstorm.darkbot.ircbot.handlers;

import java.util.*;

import org.darkstorm.darkbot.ircbot.*;
import org.darkstorm.darkbot.ircbot.events.*;
import org.darkstorm.darkbot.ircbot.irc.*;
import org.darkstorm.darkbot.ircbot.irc.constants.ServerResponseCodes;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.ircbot.irc.parsing.LineParser.MessageType;
import org.darkstorm.darkbot.ircbot.util.Tools;

public class ChannelHandler extends IRCHandler implements MessageListener {
	private Map<String, Channel> channels;
	private String joinMessage = null;
	private String partMessage = null;
	private String prefixes = "~&@%+";

	private String[] channelsToJoin;

	public ChannelHandler(IRCBot bot, IRCBotData botInfo) {
		super(bot);
		channels = new HashMap<String, Channel>();
		channelsToJoin = botInfo.channels;
		EventHandler eventHandler = bot.getEventHandler();
		eventHandler.addMessageListener(this);
	}

	public Channel newChannel(String name) {
		Channel channel = getChannel(name);
		if(channel != null)
			return channel;
		return new Channel(this, name, "");
	}

	@Override
	public void onMessageReceived(MessageEvent event) {
		Message message = event.getMessage();
		MessageType messageType = message.getType();
		if(messageType.equals(MessageType.JOIN)) {
			JoinMessage joinMessage = (JoinMessage) message;
			NicknameHandler nicknameHandler = bot.getNicknameHandler();
			String nickname = nicknameHandler.getNickname();
			Channel channel = joinMessage.getChannel();
			synchronized(channels) {
				if(!Tools.containsIgnoreCase(channels.keySet(),
						channel.getName()))
					channels.put(channel.getName(), channel);
				if(!nickname.equalsIgnoreCase(joinMessage.getUser()
						.getNickname())) {
					User[] currentUsers = channel.getUsers();
					User[] users = new User[currentUsers.length + 1];
					System.arraycopy(currentUsers, 0, users, 0,
							currentUsers.length);
					users[users.length - 1] = new User(channel, joinMessage
							.getUser().getNickname(), "");
					channel.setUsers(users);
				}
			}
		} else if(messageType.equals(MessageType.PART)) {
			PartMessage partMessage = (PartMessage) message;
			NicknameHandler nicknameHandler = bot.getNicknameHandler();
			String nickname = nicknameHandler.getNickname();
			if(nickname.equals(partMessage.getUser().getNickname())) {
				Channel channel = partMessage.getChannel();
				synchronized(channels) {
					for(String joinedChannel : channels.keySet())
						if(joinedChannel.equalsIgnoreCase(channel.getName()))
							channels.remove(joinedChannel);
				}
			}
		} else if(messageType.equals(MessageType.KICK)) {
			KickMessage kickMessage = (KickMessage) message;
			NicknameHandler nicknameHandler = bot.getNicknameHandler();
			String nickname = nicknameHandler.getNickname();
			if(nickname.equals(kickMessage.getTargetNickname())) {
				Channel channel = kickMessage.getChannel();
				synchronized(channels) {
					for(String joinedChannel : channels.keySet())
						if(joinedChannel.equalsIgnoreCase(channel.getName()))
							channels.remove(joinedChannel);
				}
			}
		} else if(messageType.equals(MessageType.SERVER)) {
			ServerResponseMessage serverResponse = (ServerResponseMessage) message;
			int responseCode = serverResponse.getResponseCode();
			String[] extraInfo = serverResponse.getExtraInfo();
			switch(responseCode) {
			case ServerResponseCodes.RPL_NAMREPLY: {
				String channelName = extraInfo[1];
				Channel channel = newChannel(channelName);
				synchronized(channels) {
					if(getChannel(channelName) == null)
						channels.put(channel.getName(), channel);
				}
				String[] userNicks = extraInfo[2].split(" ");
				User[] users = new User[userNicks.length];
				for(int i = 0; i < userNicks.length; i++) {
					String userNick = userNicks[i];
					String prefix = "";
					for(int j = 0; j < prefixes.length(); j++) {
						if(userNick.startsWith(Character.toString(prefixes
								.charAt(j)))) {
							userNick = userNick.substring(1);
							prefix = Character.toString(prefixes.charAt(j));
						}
					}
					users[i] = new User(channel, userNick, prefix);
				}
				List<User> userList = new ArrayList<User>(Arrays.asList(channel
						.getUsers()));
				newUserLabel: for(User user : users) {
					for(User currentUser : userList) {
						String nickname = user.getNickname();
						if(nickname.equalsIgnoreCase(currentUser.getNickname()))
							break newUserLabel;
					}
					userList.add(user);
				}
				channel.setUsers(userList.toArray(new User[userList.size()]));
				break;
			}
			case ServerResponseCodes.RPL_ENDOFMOTD:
			case ServerResponseCodes.ERR_NOMOTD:
				if(channelsToJoin != null) {
					synchronized(channels) {
						for(String channelToJoin : channelsToJoin) {
							Channel channel = newChannel(channelToJoin);
							channel.join();
							channels.put(channelToJoin, channel);
						}
					}
					channelsToJoin = null;
				}
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

	@Override
	public String getName() {
		return "ChannelHandler";
	}

	public String getJoinMessage() {
		return joinMessage;
	}

	public String getPartMessage() {
		return partMessage;
	}

	public String getPrefixes() {
		return prefixes;
	}

	public void setJoinMessage(String joinMessage) {
		this.joinMessage = joinMessage;
	}

	public void setPartMessage(String partMessage) {
		this.partMessage = partMessage;
	}

	public void setPrefixes(String prefixes) {
		this.prefixes = prefixes;
	}

	public String[] getChannelNames() {
		synchronized(channels) {
			Set<String> channelNameList = channels.keySet();
			return channelNameList.toArray(new String[channelNameList.size()]);
		}
	}

	public Channel[] getChannels() {
		synchronized(channels) {
			Collection<Channel> channelList = channels.values();
			return channelList.toArray(new Channel[channelList.size()]);
		}
	}

	public Channel getChannel(String name) {
		synchronized(channels) {
			return Tools.getIgnoreCase(channels, name);
		}
	}
}
