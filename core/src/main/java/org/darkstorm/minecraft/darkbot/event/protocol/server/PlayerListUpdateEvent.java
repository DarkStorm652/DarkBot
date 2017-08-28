package org.darkstorm.minecraft.darkbot.event.protocol.server;

import org.darkstorm.minecraft.darkbot.world.PlayerInfo;

public class PlayerListUpdateEvent extends PlayerListEvent {
	public PlayerListUpdateEvent(PlayerInfo playerInfo) {
		super(playerInfo);
	}
}
