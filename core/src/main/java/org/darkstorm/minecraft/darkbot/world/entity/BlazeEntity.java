package org.darkstorm.darkbot.minecraftbot.world.entity;

import org.darkstorm.darkbot.minecraftbot.util.IntHashMap;
import org.darkstorm.darkbot.minecraftbot.world.World;

public class BlazeEntity extends AggressiveEntity {
	private boolean burning;

	public BlazeEntity(World world, int id) {
		super(world, id);
	}

	public boolean isBurning() {
		return burning;
	}

	public void setBurning(boolean burning) {
		this.burning = burning;
	}

	@Override
	public void updateMetadata(IntHashMap<WatchableObject> metadata) {
		super.updateMetadata(metadata);
		if(metadata.containsKey(16))
			setBurning(((Byte) metadata.get(16).getObject()).byteValue() == 1);
	}
}
