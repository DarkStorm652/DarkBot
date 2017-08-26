package org.darkstorm.minecraft.darkbot.world.entity;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
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
	public void updateMetadata(EntityMetadata[] metadata) {
		super.updateMetadata(metadata);

		for(EntityMetadata md : metadata) {
			if(md.getId() == 16)
				setIgnited((Byte) md.getValue() == 1);
			if(md.getId() == 17)
				setCharged((Byte) md.getValue() == 1);
		}
	}
}
