package org.darkstorm.darkbot.minecraftbot.event.protocol.server;

import org.darkstorm.darkbot.minecraftbot.util.IntHashMap;
import org.darkstorm.darkbot.minecraftbot.world.entity.WatchableObject;
import org.darkstorm.darkbot.minecraftbot.world.item.ItemStack;

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
