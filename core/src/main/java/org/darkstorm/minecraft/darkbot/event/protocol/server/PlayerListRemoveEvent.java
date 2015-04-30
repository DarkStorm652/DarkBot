package org.darkstorm.minecraft.darkbot.event.protocol.server;


public class PlayerListRemoveEvent extends PlayerListEvent {
	public PlayerListRemoveEvent(String playerName) {
		super(playerName);
	}
}
