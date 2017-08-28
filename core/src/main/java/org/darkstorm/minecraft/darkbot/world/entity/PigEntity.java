package org.darkstorm.minecraft.darkbot.world.entity;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import org.darkstorm.minecraft.darkbot.*;
import org.darkstorm.minecraft.darkbot.event.protocol.client.EntityUseEvent;
import org.darkstorm.minecraft.darkbot.world.World;

public class PigEntity extends PassiveEntity {
	protected boolean saddled;

	public PigEntity(World world, int id) {
		super(world, id);
	}

	public boolean isSaddled() {
		return saddled;
	}

	public void setSaddled(boolean saddled) {
		this.saddled = saddled;
	}

	public void ride() {
		if(!saddled)
			return;
		MinecraftBot bot = world.getBot();
		bot.getEventBus().fire(new EntityUseEvent(this));
	}

	@Override
	public void updateMetadata(EntityMetadata[] metadata) {
		super.updateMetadata(metadata);

		for(EntityMetadata md : metadata) {
			if(md.getId() == 16)
				setSaddled((Byte) md.getValue() == 1);
		}
	}
}
