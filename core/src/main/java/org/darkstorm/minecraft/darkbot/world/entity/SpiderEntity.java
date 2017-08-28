package org.darkstorm.minecraft.darkbot.world.entity;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import org.darkstorm.minecraft.darkbot.util.IntHashMap;
import org.darkstorm.minecraft.darkbot.world.World;

public class SpiderEntity extends AggressiveEntity {
	protected boolean aggravated;

	public SpiderEntity(World world, int id) {
		super(world, id);
	}

	public boolean isAggravated() {
		return aggravated;
	}

	public void setAggravated(boolean aggravated) {
		this.aggravated = aggravated;
	}

	@Override
	public void updateMetadata(EntityMetadata[] metadata) {
		super.updateMetadata(metadata);
		for(EntityMetadata md : metadata) {
			if(md.getId() == 16)
				setAggravated((Byte) md.getValue() == 1);
		}
	}
}
