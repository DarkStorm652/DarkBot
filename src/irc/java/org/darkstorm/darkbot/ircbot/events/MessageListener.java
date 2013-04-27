package org.darkstorm.darkbot.ircbot.events;

public interface MessageListener {
	public void onRawSent(MessageEvent event);

	public void onMessageSent(MessageEvent event);

	public void onMessageReceived(MessageEvent event);

	public void onNoticeSent(MessageEvent event);
}
