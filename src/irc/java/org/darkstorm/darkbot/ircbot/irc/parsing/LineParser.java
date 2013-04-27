package org.darkstorm.darkbot.ircbot.irc.parsing;

import org.darkstorm.darkbot.ircbot.IRCBot;
import org.darkstorm.darkbot.ircbot.handlers.*;
import org.darkstorm.darkbot.ircbot.irc.Channel;
import org.darkstorm.darkbot.ircbot.irc.messages.*;
import org.darkstorm.darkbot.tools.StringTools;

/**
 * @Created Mar 7, 2010 at 8:46:10 AM
 * @author DarkStorm
 */
public class LineParser {

	public static enum MessageType {
		CHANNEL_USER_MODE,
		CHANNEL_MODE,
		USER_MODE,
		MESSAGE,
		NOTICE,
		INVITE,
		SERVER,
		PING,
		PONG,
		NICK,
		JOIN,
		PART,
		KICK,
		QUIT,
		OTHER
	}

	private MessageHandler messageHandler;

	public LineParser(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}

	public Message parse(String raw) {
		String[] components = getComponents(raw);
		MessageType type = parseType(components);
		return parseMessage(type, raw, components);
	}

	private String[] getComponents(String inputLine) {
		String[] components = StringTools.split(inputLine, ":");
		if(components.length > 2) {
			String message = "";
			for(int i = 2; i < components.length; i++) {
				message += components[i];
				if(i < components.length - 1)
					message += ":";
			}
			return new String[] { components[0], components[1], message };
		}
		return components;
	}

	private MessageType parseType(String[] components) {
		String[] senderInfo = components[1].split(" ");
		int exclamationIndex = senderInfo[0].indexOf('!');
		int atIndex = senderInfo[0].indexOf('@');
		if(components[0].startsWith("PING ")) {
			return MessageType.PING;
		} else if(components[1].contains(" PRIVMSG ")) {
			return MessageType.MESSAGE;
		} else if((exclamationIndex == -1 || atIndex == -1 || exclamationIndex >= atIndex)
				&& StringTools.isInteger(senderInfo[1])) {
			return MessageType.SERVER;
		} else if((exclamationIndex == -1 || atIndex == -1 || exclamationIndex >= atIndex)
				&& senderInfo[1].equals("MODE")) {
			return MessageType.USER_MODE;
		} else if(components[1].contains(" MODE ") && exclamationIndex != -1
				&& Channel.isChannel(senderInfo[2])) {
			if(senderInfo.length > 4)
				return MessageType.CHANNEL_USER_MODE;
			else
				return MessageType.CHANNEL_MODE;
		} else {
			for(MessageType messageType : MessageType.values()) {
				String type = messageType.toString();
				if(components[1].contains(type)) {
					return messageType;
				}
			}
		}
		return MessageType.OTHER;
	}

	private Message parseMessage(MessageType type, String raw,
			String[] components) {
		switch(type) {
		case MESSAGE:
		case NOTICE:
			return parseUserMessage(type, raw, components);
		case JOIN:
		case PART:
			return parseJoinPartMessage(type, raw, components);
		case KICK:
			return parseKickMessage(type, raw, components);
		case INVITE:
			return parseInviteMessage(type, raw, components);
		case USER_MODE:
		case CHANNEL_MODE:
		case CHANNEL_USER_MODE:
			return parseModeMessage(type, raw, components);
		case NICK:
			return parseNickMessage(type, raw, components);
		case SERVER:
			return parseServerResponseMessage(type, raw, components);
		case PING:
		case PONG:
			return parsePingPongMessage(type, raw, components);
		case QUIT:
			return parseQuitMessage(type, raw, components);
		case OTHER:
			return new UnknownMessage(raw);
		default:
			throw new RuntimeException("Illegal message type");
		}
	}

	private UserMessage parseUserMessage(MessageType type, String raw,
			String[] components) {
		UserInfo sender;
		String receiver;
		String senderInfo = components[1];
		String[] senderInfoParts = components[1].split(" ");
		int exclamationIndex = senderInfoParts[0].indexOf('!');
		int atIndex = senderInfoParts[0].indexOf('@');
		if(exclamationIndex != -1 && atIndex != -1
				&& exclamationIndex < atIndex) {
			String[] exclamationParts = StringTools.split(senderInfo, "!");
			String[] atParts = StringTools.split(exclamationParts[1], "@");
			String senderNickname = exclamationParts[0];
			String senderUsername = atParts[0];
			String senderHostname = atParts[1].split(" ")[0];
			sender = new UserInfo(senderNickname, senderUsername,
					senderHostname);
		} else
			sender = new UserInfo(senderInfoParts[0], null, null);

		if(type == MessageType.MESSAGE) {
			int lastIndex = senderInfo.lastIndexOf(" PRIVMSG ");
			int length = (" PRIVMSG ").length();
			String nameTrailingSpace = senderInfo.substring(lastIndex + length);
			receiver = nameTrailingSpace.trim();

		} else if(type == MessageType.NOTICE) {
			String unreplacedSpace = components[1].substring(components[1]
					.lastIndexOf(" NOTICE ") + (" NOTICE ").length());
			receiver = unreplacedSpace.substring(0,
					unreplacedSpace.lastIndexOf(" "));
		} else
			throw new RuntimeException(
					"UserMessage parsed with wrong type (how!?)");

		String message = components[2];
		if(message.startsWith("\u0001") && message.endsWith("\u0001")) {
			message = message.substring(1, message.length() - 1);
			return new UserMessage(type, raw, sender, receiver, message, true);
		}
		return new UserMessage(type, raw, sender, receiver, message, false);
	}

	private Message parseJoinPartMessage(MessageType type, String raw,
			String[] components) {
		String senderInfo = components[1];
		String[] exclamationParts = StringTools.split(senderInfo, "!");
		String[] atParts = StringTools.split(exclamationParts[1], "@");
		String senderNickname = exclamationParts[0];
		String senderUsername = atParts[0];
		String[] senderInfoAfterAt = atParts[1].split(" ");
		String senderHostname = senderInfoAfterAt[0];
		UserInfo user = new UserInfo(senderNickname, senderUsername,
				senderHostname);

		IRCBot bot = messageHandler.getBot();
		ChannelHandler channelHandler = bot.getChannelHandler();
		String channelName;
		if(senderInfoAfterAt.length >= 3)
			channelName = senderInfoAfterAt[2];
		else
			channelName = components[2];
		Channel channel = channelHandler.getChannel(channelName);
		if(channel == null)
			channel = new Channel(channelHandler, channelName, "");
		String message = null;
		if(type == MessageType.PART && components.length > 2)
			message = components[2];

		if(type == MessageType.JOIN)
			return new JoinMessage(raw, user, channel);
		else if(type == MessageType.PART)
			return new PartMessage(raw, user, channel, message);
		else
			throw new RuntimeException(
					"JoinMessage/PartMessage parsed with wrong type (how!?)");
	}

	private Message parseKickMessage(MessageType type, String raw,
			String[] components) {
		String senderInfo = components[1];
		String[] senderInfoParts = senderInfo.split(" ");
		String[] exclamationParts = StringTools.split(senderInfo, "!");
		String[] atParts = StringTools.split(exclamationParts[1], "@");
		String senderNickname = exclamationParts[0];
		String senderUsername = atParts[0];
		String[] senderInfoAfterAt = atParts[1].split(" ");
		String senderHostname = senderInfoAfterAt[0];
		UserInfo sender = new UserInfo(senderNickname, senderUsername,
				senderHostname);

		IRCBot bot = messageHandler.getBot();
		ChannelHandler channelHandler = bot.getChannelHandler();
		String channelName = senderInfoParts[2];
		Channel channel = channelHandler.getChannel(channelName);
		if(channel == null)
			channel = new Channel(channelHandler, channelName, "");

		String target = senderInfoParts[3];

		String message = null;
		if(components.length > 2)
			message = components[2];
		return new KickMessage(raw, sender, channel, target, message);
	}

	private Message parseInviteMessage(MessageType type, String raw,
			String[] components) {
		String senderInfo = components[1];
		String[] exclamationParts = StringTools.split(senderInfo, "!");
		String[] atParts = StringTools.split(exclamationParts[1], "@");
		String senderNickname = exclamationParts[0];
		String senderUsername = atParts[0];
		String[] senderInfoAfterAt = atParts[1].split(" ");
		String senderHostname = senderInfoAfterAt[0];
		UserInfo sender = new UserInfo(senderNickname, senderUsername,
				senderHostname);

		String receiver = senderInfoAfterAt[2];

		IRCBot bot = messageHandler.getBot();
		ChannelHandler channelHandler = bot.getChannelHandler();
		Channel channel = new Channel(channelHandler, components[2], "");

		return new InviteMessage(raw, sender, receiver, channel);
	}

	private Message parseModeMessage(MessageType type, String raw,
			String[] components) {
		String senderInfo = components[1];
		String[] senderInfoParts = senderInfo.split(" ");
		if(type == MessageType.CHANNEL_MODE
				|| type == MessageType.CHANNEL_USER_MODE) {
			String[] exclamationParts = StringTools.split(senderInfo, "!");
			String[] atParts = StringTools.split(exclamationParts[1], "@");
			String senderNickname = exclamationParts[0];
			String senderUsername = atParts[0];
			String[] senderInfoAfterAt = atParts[1].split(" ");
			String senderHostname = senderInfoAfterAt[0];
			UserInfo sender = new UserInfo(senderNickname, senderUsername,
					senderHostname);

			String channel = senderInfoParts[2];
			String mode = senderInfoParts[3];
			String[] target;
			if(senderInfoParts.length > 4) {
				target = new String[senderInfoParts.length - 4];
				for(int i = 4; i < senderInfoParts.length; i++)
					target[i - 4] = senderInfoParts[i];
			} else
				target = new String[0];
			return new ModeMessage(type, raw, sender, channel, target, mode);
		} else if(type == MessageType.USER_MODE) {
			UserInfo sender = new UserInfo(senderInfoParts[0], null, null);
			String receiver = senderInfoParts[2];
			String mode = components[2];
			return new ModeMessage(type, raw, sender, receiver, new String[0],
					mode);
		}
		return null;
	}

	private Message parseNickMessage(MessageType type, String raw,
			String[] components) {
		String senderInfo = components[1];
		String[] exclamationParts = StringTools.split(senderInfo, "!");
		String[] atParts = StringTools.split(exclamationParts[1], "@");
		String senderNickname = exclamationParts[0];
		String senderUsername = atParts[0];
		String[] senderInfoAfterAt = atParts[1].split(" ");
		String senderHostname = senderInfoAfterAt[0];
		UserInfo user = new UserInfo(senderNickname, senderUsername,
				senderHostname);
		return new NickMessage(raw, user, components[2]);
	}

	private Message parseServerResponseMessage(MessageType type, String raw,
			String[] components) {
		String[] senderInfoParts = components[1].split(" ");
		String serverName = senderInfoParts[0];
		int responseCode = Integer.parseInt(senderInfoParts[1]);
		String target = senderInfoParts[2];
		int extraInfoAmount = senderInfoParts.length - 3;
		if(components.length > 2)
			extraInfoAmount += 1;
		String[] extraInfo = new String[extraInfoAmount];
		for(int i = 3; i < senderInfoParts.length; i++)
			extraInfo[i - 3] = senderInfoParts[i];
		if(components.length > 2)
			extraInfo[extraInfo.length - 1] = components[2];
		return new ServerResponseMessage(raw, serverName, responseCode, target,
				extraInfo);
	}

	private Message parsePingPongMessage(MessageType type, String raw,
			String[] components) {
		String message = null;
		if(components.length > 1)
			message = components[1];
		return new PingMessage(type, raw, message);
	}

	private Message parseQuitMessage(MessageType type, String raw,
			String[] components) {
		String senderInfo = components[1];
		String[] exclamationParts = StringTools.split(senderInfo, "!");
		String[] atParts = StringTools.split(exclamationParts[1], "@");
		String senderNickname = exclamationParts[0];
		String senderUsername = atParts[0];
		String[] senderInfoAfterAt = atParts[1].split(" ");
		String senderHostname = senderInfoAfterAt[0];
		UserInfo user = new UserInfo(senderNickname, senderUsername,
				senderHostname);
		String message = null;
		if(components.length > 2)
			message = components[2];
		return new QuitMessage(raw, user, message);
	}

	public MessageHandler getMessageHandler() {
		return messageHandler;
	}
}
