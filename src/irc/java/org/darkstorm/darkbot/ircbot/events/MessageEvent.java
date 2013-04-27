package org.darkstorm.darkbot.ircbot.events;

import org.darkstorm.darkbot.ircbot.handlers.MessageHandler;
import org.darkstorm.darkbot.ircbot.irc.messages.Message;

public class MessageEvent extends Event {
	public static final int RAW_MESSAGE_SENT_ID = 0;
	public static final int MESSAGE_SENT_ID = 1;
	public static final int MESSAGE_RECEIVED_ID = 2;
	public static final int NOTICE_SENT_ID = 3;

	public MessageEvent(MessageHandler source, int id, Message message) {
		super(source, System.currentTimeMillis(), new Object[] { id, message });
	}

	@Override
	public MessageHandler getSource() {
		return (MessageHandler) super.getSource();
	}

	public int getId() {
		return (Integer) getArgumentAt(0);
	}

	public Message getMessage() {
		return (Message) getArgumentAt(1);
	}

}
