package org.darkstorm.minecraft.darkbot.event.protocol.server;

import org.darkstorm.minecraft.darkbot.world.PlayerInfo;

public class PlayerListRemoveEvent extends PlayerListEvent {
	public PlayerListRemoveEvent(PlayerInfo playerInfo) {
		super(playerInfo);
	}
}
