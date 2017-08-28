package org.darkstorm.minecraft.darkbot.world.entity;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
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
	public void updateMetadata(EntityMetadata[] metadata) {
		super.updateMetadata(metadata);

		for(EntityMetadata md : metadata) {
			if(md.getId() == 10)
				setItem((ItemStack) md.getValue());
		}
	}
}
