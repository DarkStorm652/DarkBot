package org.darkstorm.darkbot.minecraftbot.world.entity;

import org.darkstorm.darkbot.minecraftbot.world.World;
import org.darkstorm.darkbot.minecraftbot.world.item.ItemStack;

public class PlayerEntity extends LivingEntity {
	private String name;
	private ItemStack[] armor = new ItemStack[4];
	private ItemStack heldItem = null;

	public PlayerEntity(World world, int id, String name) {
		super(world, id);
		this.name = name;
		
		sizeX = 0.6;
		sizeY = 1.8;
		sizeZ = 0.6;
	}

	public String getName() {
		return name;
	}

	@Override
	public ItemStack getWornItemAt(int slot) {
		return slot == 0 ? heldItem
				: slot > 0 && slot <= armor.length ? armor[slot - 1] : null;
	}

	@Override
	public void setWornItemAt(int slot, ItemStack item) {
		if(slot == 0)
			heldItem = item;
		else if(slot > 0 && slot <= armor.length)
			armor[slot - 1] = item;
	}
}
