package org.darkstorm.darkbot.minecraftbot.world.entity;

import org.darkstorm.darkbot.minecraftbot.util.IntHashMap;
import org.darkstorm.darkbot.minecraftbot.world.World;

public class CaveSpiderEntity extends AggressiveEntity {
	protected boolean aggravated;

	public CaveSpiderEntity(World world, int id) {
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
