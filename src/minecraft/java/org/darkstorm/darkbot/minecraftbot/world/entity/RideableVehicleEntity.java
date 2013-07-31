package org.darkstorm.darkbot.minecraftbot.world.entity;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.events.protocol.client.EntityUseEvent;
import org.darkstorm.darkbot.minecraftbot.world.World;

public abstract class RideableVehicleEntity extends VehicleEntity {
	public RideableVehicleEntity(World world, int id) {
		super(world, id);
	}

	public void ride() {
		MinecraftBot bot = world.getBot();
		bot.getEventManager().sendEvent(new EntityUseEvent(this));
	}
}
