package org.darkstorm.darkbot.minecraftbot.world.entity;

import org.darkstorm.darkbot.minecraftbot.util.IntHashMap;
import org.darkstorm.darkbot.minecraftbot.world.World;

public class OcelotEntity extends TameableEntity {
	private int skinType;

	public OcelotEntity(World world, int id) {
		super(world, id);
	}

	@Override
	public boolean isAggressive() {
		return false;
	}

	public int getSkinType() {
		return skinType;
	}

	@Override
	public void setAggressive(boolean aggressive) {
	}

	public void setSkinType(int skinType) {
		this.skinType = skinType;
	}

	@Override
	public void updateMetadata(IntHashMap<WatchableObject> metadata) {
		super.updateMetadata(metadata);
		if(metadata.containsKey(16)) {
			byte flags = (Byte) metadata.get(16).getObject();
			setSitting((flags & 1) != 0);
			setTamed((flags & 2) != 0);
		}

		if(metadata.containsKey(17))
			setOwnerName((String) metadata.get(17).getObject());
		if(metadata.containsKey(18))
			setSkinType((Integer) metadata.get(18).getObject());
	}
}
