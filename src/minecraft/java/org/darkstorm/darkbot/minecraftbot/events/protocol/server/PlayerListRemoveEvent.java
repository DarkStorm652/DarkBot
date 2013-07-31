package org.darkstorm.darkbot.minecraftbot.events.protocol.server;


public class PlayerListRemoveEvent extends PlayerListEvent {
	public PlayerListRemoveEvent(String playerName) {
		super(playerName);
	}
}
