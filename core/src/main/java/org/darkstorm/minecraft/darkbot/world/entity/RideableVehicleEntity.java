package org.darkstorm.minecraft.darkbot.world.entity;

import org.darkstorm.minecraft.darkbot.MinecraftBot;
import org.darkstorm.minecraft.darkbot.event.protocol.client.EntityUseEvent;
import org.darkstorm.minecraft.darkbot.world.World;

public abstract class RideableVehicleEntity extends VehicleEntity {
	public RideableVehicleEntity(World world, int id) {
		super(world, id);
	}

	public void ride() {
		MinecraftBot bot = world.getBot();
		bot.getEventBus().fire(new EntityUseEvent(this));
	}
}
