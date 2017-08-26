package org.darkstorm.minecraft.darkbot.event.protocol.server;

import org.darkstorm.minecraft.darkbot.event.protocol.ProtocolEvent;
import org.darkstorm.minecraft.darkbot.world.PlayerInfo;

public abstract class PlayerListEvent extends ProtocolEvent {
	private final PlayerInfo playerInfo;

	public PlayerListEvent(PlayerInfo playerInfo) {
		this.playerInfo = playerInfo;
	}

	public PlayerInfo getPlayerInfo() {
		return playerInfo;
	}
}
