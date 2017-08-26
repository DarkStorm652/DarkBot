package org.darkstorm.minecraft.darkbot.world.entity;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import org.darkstorm.minecraft.darkbot.util.IntHashMap;
import org.darkstorm.minecraft.darkbot.world.World;

public class WolfEntity extends TameableEntity {
	public WolfEntity(World world, int id) {
		super(world, id);
	}

	@Override
	public void updateMetadata(EntityMetadata[] metadata) {
		super.updateMetadata(metadata);
		for(EntityMetadata md : metadata) {
			if(md.getId() == 16) {
				byte flags = (Byte) md.getValue();
				setSitting((flags & 1) != 0);
				setAggressive((flags & 2) != 0);
				setTamed((flags & 4) != 0);
			}
			if(md.getId() == 17)
				setOwnerName((String) md.getValue());
			if(md.getId() == 18)
				setHealth((Integer) md.getValue());

		}
	}
}
