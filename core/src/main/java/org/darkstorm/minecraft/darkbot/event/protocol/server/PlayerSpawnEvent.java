package org.darkstorm.minecraft.darkbot.event.protocol.server;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;

import org.darkstorm.minecraft.darkbot.world.item.ItemStack;

public class PlayerSpawnEvent extends MetaEntitySpawnEvent {
	private final String playerUUID;
	private final ItemStack heldItem;

	public PlayerSpawnEvent(int playerId, String playerUUID, ItemStack heldItem, RotatedSpawnLocation location, EntityMetadata[] metadata) {
		super(playerId, location, metadata);

		this.playerUUID = playerUUID;
		this.heldItem = heldItem;
	}

	public String getPlayerUUID() {
		return playerUUID;
	}

	public ItemStack getHeldItem() {
		return heldItem;
	}
}
