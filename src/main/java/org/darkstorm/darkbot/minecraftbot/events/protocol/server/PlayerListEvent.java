package org.darkstorm.darkbot.minecraftbot.events.protocol.server;

import org.darkstorm.darkbot.minecraftbot.events.protocol.ProtocolEvent;

public abstract class PlayerListEvent extends ProtocolEvent {
	private final String playerName;

	public PlayerListEvent(String playerName) {
		this.playerName = playerName;
	}

	public String getPlayerName() {
		return playerName;
	}
}
