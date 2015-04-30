package org.darkstorm.minecraft.darkbot.event.protocol.server;

import org.darkstorm.minecraft.darkbot.IntHashMap;
import org.darkstorm.minecraft.darkbot.world.entity.WatchableObject;
import org.darkstorm.minecraft.darkbot.world.item.ItemStack;

public class PlayerSpawnEvent extends MetaEntitySpawnEvent {
	private final String playerName;
	private final ItemStack heldItem;

	public PlayerSpawnEvent(int playerId, String playerName, ItemStack heldItem, RotatedSpawnLocation location, IntHashMap<WatchableObject> metadata) {
		super(playerId, location, metadata);

		this.playerName = playerName;
		this.heldItem = heldItem;
	}

	public String getPlayerName() {
		return playerName;
	}

	public ItemStack getHeldItem() {
		return heldItem;
	}
}
