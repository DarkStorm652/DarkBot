package org.darkstorm.minecraft.darkbot.world.entity;

import org.darkstorm.minecraft.darkbot.util.IntHashMap;
import org.darkstorm.minecraft.darkbot.world.World;
import org.darkstorm.minecraft.darkbot.world.item.ItemStack;

public class ItemEntity extends Entity {
	private ItemStack item;

	public ItemEntity(World world, int id) {
		super(world, id);
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	@Override
	public void updateMetadata(IntHashMap<WatchableObject> metadata) {
		super.updateMetadata(metadata);
		if(metadata.containsKey(10))
			setItem((ItemStack) metadata.get(10).getObject());
	}
}
