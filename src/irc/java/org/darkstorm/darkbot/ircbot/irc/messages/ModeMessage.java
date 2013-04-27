package org.darkstorm.darkbot.ircbot.irc.messages;

import org.darkstorm.darkbot.ircbot.irc.parsing.LineParser.MessageType;
import org.darkstorm.darkbot.ircbot.irc.parsing.*;

public class ModeMessage extends Message {
	private final UserInfo sender;
	private String receiverNickname;
	private String[] targets;
	private String mode;

	public ModeMessage(MessageType type, String raw, UserInfo sender,
			String receiverNickname, String[] targets, String mode) {
		super(type, raw);
		if(!type.equals(MessageType.USER_MODE)
				&& !type.equals(MessageType.CHANNEL_MODE)
				&& !type.equals(MessageType.CHANNEL_USER_MODE))
			throw new IllegalArgumentException("type is not USER_MODE, "
					+ "CHANNEL_MODE, or CHANNEL_USER_MODE");
		this.sender = sender;
		this.receiverNickname = receiverNickname;
		this.targets = targets;
		this.mode = mode;
	}

	public UserInfo getSender() {
		return sender;
	}

	public String getReceiverNickname() {
		return receiverNickname;
	}

	/**
	 * This is only for setting a user mode within a channel, and will be the
	 * targeted user of the mode change
	 */
	public String[] getTargets() {
		return targets;
	}

	public String getMode() {
		return mode;
	}
}
