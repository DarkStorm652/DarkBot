package org.darkstorm.minecraft.darkbot.world.entity;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import org.darkstorm.minecraft.darkbot.world.World;
import org.darkstorm.minecraft.darkbot.world.item.ItemStack;

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
	public void updateMetadata(EntityMetadata[] metadata) {
		super.updateMetadata(metadata);

		for(EntityMetadata md : metadata) {
			if(md.getId() == 2)
				item = (ItemStack) md.getValue();
			else if(md.getId() == 3)
				direction = (Integer) md.getValue();
		}
	}
}
