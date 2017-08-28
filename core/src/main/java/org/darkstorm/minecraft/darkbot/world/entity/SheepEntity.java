package org.darkstorm.minecraft.darkbot.world.entity;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import org.darkstorm.minecraft.darkbot.util.IntHashMap;
import org.darkstorm.minecraft.darkbot.world.World;

public class SheepEntity extends PassiveEntity {
	protected boolean sheared;
	protected int color;

	public SheepEntity(World world, int id) {
		super(world, id);
	}

	public boolean isSheared() {
		return sheared;
	}

	public int getColor() {
		return color;
	}

	public void setSheared(boolean sheared) {
		this.sheared = sheared;
	}

	public void setColor(int color) {
		this.color = color & 0xF;
	}

	@Override
	public void updateMetadata(EntityMetadata[] metadata) {
		super.updateMetadata(metadata);

		for(EntityMetadata md : metadata) {
			if (md.getId() == 16) {
				byte data = (Byte) md.getValue();
				setColor(data & 0xF);
				setSheared((data & 0x10) != 0);
			}
		}
	}
}
