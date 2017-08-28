package org.darkstorm.minecraft.darkbot.world.entity;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
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
	public void updateMetadata(EntityMetadata[] metadata) {
		super.updateMetadata(metadata);

		for(EntityMetadata md : metadata) {
			if(md.getId() == 16)
				setHeldItemId((Byte) md.getValue());
			if(md.getId() == 17)
				setAggravated((Byte) md.getValue() == 1);
		}
	}
}