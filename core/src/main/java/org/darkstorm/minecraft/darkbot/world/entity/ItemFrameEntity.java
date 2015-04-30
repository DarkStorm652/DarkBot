package org.darkstorm.darkbot.minecraftbot.world.entity;

import org.darkstorm.darkbot.minecraftbot.util.IntHashMap;
import org.darkstorm.darkbot.minecraftbot.world.World;
import org.darkstorm.darkbot.minecraftbot.world.item.ItemStack;

public class ItemFrameEntity extends Entity {
	private ItemStack item;
	private int direction;

	public ItemFrameEntity(World world, int id) {
		super(world, id);
	}

	public ItemStack getItem() {
		return item;
	}

	public int getDirection() {
		return direction;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	@Override
	public void updateMetadata(IntHashMap<WatchableObject> metadata) {
		super.updateMetadata(metadata);
		if(metadata.containsKey(2))
			item = (ItemStack) metadata.get(2).getObject();
		if(metadata.containsKey(3))
			direction = (Integer) metadata.get(3).getObject();
	}
}
