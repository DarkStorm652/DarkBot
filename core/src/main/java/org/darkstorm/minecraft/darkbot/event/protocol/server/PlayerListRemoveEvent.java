package org.darkstorm.darkbot.minecraftbot.event.protocol.server;


public class PlayerListRemoveEvent extends PlayerListEvent {
	public PlayerListRemoveEvent(String playerName) {
		super(playerName);
	}
}
