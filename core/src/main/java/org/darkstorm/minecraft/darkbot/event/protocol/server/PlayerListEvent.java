package org.darkstorm.minecraft.darkbot.event.protocol.server;

import org.darkstorm.minecraft.darkbot.event.protocol.ProtocolEvent;

public abstract class PlayerListEvent extends ProtocolEvent {
	private final String playerName;

	public PlayerListEvent(String playerName) {
		this.playerName = playerName;
	}

	public String getPlayerName() {
		return playerName;
	}
}
