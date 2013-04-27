package org.darkstorm.darkbot.minecraftbot.world.entity;

import org.darkstorm.darkbot.minecraftbot.world.World;
import org.darkstorm.darkbot.minecraftbot.world.item.ItemStack;

public class ItemEntity extends Entity {
	private final ItemStack item;

	public ItemEntity(World world, int id, ItemStack item) {
		super(world, id);
		this.item = item;
	}

	public ItemStack getItem() {
		return item;
	}
}
