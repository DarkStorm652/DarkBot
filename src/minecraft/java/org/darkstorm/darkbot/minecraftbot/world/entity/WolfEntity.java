package org.darkstorm.darkbot.minecraftbot.world.entity;

import org.darkstorm.darkbot.minecraftbot.util.IntHashMap;
import org.darkstorm.darkbot.minecraftbot.world.World;

public class WolfEntity extends TameableEntity {
	public WolfEntity(World world, int id) {
		super(world, id);
	}

	@Override
	public void updateMetadata(IntHashMap<WatchableObject> metadata) {
		super.updateMetadata(metadata);
		if(metadata.containsKey(16)) {
			byte flags = (Byte) metadata.get(16).getObject();
			setSitting((flags & 1) == 1);
			setAggressive((flags & 2) == 1);
			setTamed((flags & 4) == 1);
		}

		if(metadata.containsKey(17))
			setOwnerName((String) metadata.get(17).getObject());
		if(metadata.containsKey(18))
			setHealth((Integer) metadata.get(18).getObject());
	}
}
