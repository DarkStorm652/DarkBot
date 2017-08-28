package org.darkstorm.minecraft.darkbot.world.entity;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import org.darkstorm.minecraft.darkbot.util.IntHashMap;
import org.darkstorm.minecraft.darkbot.world.World;

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
	public void updateMetadata(EntityMetadata[] metadata) {
		super.updateMetadata(metadata);
		for(EntityMetadata md : metadata) {
			if(md.getId() == 16) {
				byte flags = (Byte) md.getValue();
				setSitting((flags & 1) != 0);
				setTamed((flags & 2) != 0);
			}
			if(md.getId() == 17)
				setOwnerName((String) md.getValue());
			if(md.getId() == 18)
				setSkinType((Integer) md.getValue());

		}
	}
}
