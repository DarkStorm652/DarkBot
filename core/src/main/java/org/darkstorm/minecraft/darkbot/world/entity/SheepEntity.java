package org.darkstorm.minecraft.darkbot.world.entity;

import org.darkstorm.minecraft.darkbot.IntHashMap;
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
	public void updateMetadata(IntHashMap<WatchableObject> metadata) {
		super.updateMetadata(metadata);
		if(metadata.containsKey(16)) {
			byte data = (Byte) metadata.get(16).getObject();
			setColor(data & 0xF);
			setSheared((data & 0x10) != 0);
		}
	}
}
