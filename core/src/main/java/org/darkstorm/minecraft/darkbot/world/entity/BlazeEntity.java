package org.darkstorm.minecraft.darkbot.world.entity;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import org.darkstorm.minecraft.darkbot.util.IntHashMap;
import org.darkstorm.minecraft.darkbot.world.World;

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
	public void updateMetadata(EntityMetadata[] metadata) {
		super.updateMetadata(metadata);

		for(EntityMetadata md : metadata) {
			if(md.getId() == 16)
				setBurning((Byte) md.getValue() == 1);
		}
	}
}
