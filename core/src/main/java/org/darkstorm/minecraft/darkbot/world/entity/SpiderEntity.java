package org.darkstorm.minecraft.darkbot.world.entity;

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
	public void updateMetadata(IntHashMap<WatchableObject> metadata) {
		super.updateMetadata(metadata);
		if(metadata.containsKey(16))
			setAggravated(((Byte) metadata.get(16).getObject()).byteValue() == 1);
	}
}
