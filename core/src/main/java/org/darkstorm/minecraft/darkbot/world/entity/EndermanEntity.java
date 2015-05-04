package org.darkstorm.minecraft.darkbot.world.entity;

import org.darkstorm.minecraft.darkbot.util.IntHashMap;
import org.darkstorm.minecraft.darkbot.world.World;

public class EndermanEntity extends AggressiveEntity {
	protected int heldItemId;
	protected boolean aggravated = false;

	public EndermanEntity(World world, int id) {
		super(world, id);
	}

	public boolean isAggravated() {
		return aggravated;
	}

	public int getHeldItemId() {
		return heldItemId;
	}

	public void setAggravated(boolean aggravated) {
		this.aggravated = aggravated;
	}

	public void setHeldItemId(int heldItemId) {
		this.heldItemId = heldItemId;
	}

	@Override
	public void updateMetadata(IntHashMap<WatchableObject> metadata) {
		super.updateMetadata(metadata);
		if(metadata.containsKey(16))
			setHeldItemId((Byte) metadata.get(16).getObject());
		if(metadata.containsKey(17))
			setAggravated(((Byte) metadata.get(17).getObject()).byteValue() == 1);
	}
}
