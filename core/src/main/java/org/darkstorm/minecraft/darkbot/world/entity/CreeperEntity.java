package org.darkstorm.minecraft.darkbot.world.entity;

import org.darkstorm.minecraft.darkbot.IntHashMap;
import org.darkstorm.minecraft.darkbot.world.World;

public class CreeperEntity extends AggressiveEntity {
	protected boolean ignited, charged;

	public CreeperEntity(World world, int id) {
		super(world, id);
	}

	public boolean isIgnited() {
		return ignited;
	}

	public boolean isCharged() {
		return charged;
	}

	public void setIgnited(boolean ignited) {
		this.ignited = ignited;
	}

	public void setCharged(boolean charged) {
		this.charged = charged;
	}

	@Override
	public void updateMetadata(IntHashMap<WatchableObject> metadata) {
		super.updateMetadata(metadata);
		if(metadata.containsKey(16))
			setIgnited(((Byte) metadata.get(16).getObject()).byteValue() == 1);
		if(metadata.containsKey(17))
			setCharged(((Byte) metadata.get(17).getObject()).byteValue() == 1);
	}
}
