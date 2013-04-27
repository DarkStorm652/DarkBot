package org.darkstorm.darkbot.ircbot.events;

public interface DCCListener {
	public void onIncomingFileTransfer(DCCEvent event);

	public void onIncomingChatRequest(DCCEvent event);

	public void onFileTransferFinished(DCCEvent event);
}
