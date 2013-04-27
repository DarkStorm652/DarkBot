package org.darkstorm.darkbot.minecraftbot.world.entity;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.handlers.ConnectionHandler;
import org.darkstorm.darkbot.minecraftbot.protocol.writeable.Packet7UseEntity;
import org.darkstorm.darkbot.minecraftbot.world.World;

public abstract class StorageVehicleEntity extends VehicleEntity {

	public StorageVehicleEntity(World world, int id) {
		super(world, id);
	}

	public void open() {
		MinecraftBot bot = world.getBot();
		ConnectionHandler connectionHandler = bot.getConnectionHandler();
		Packet7UseEntity useEntityPacket = new Packet7UseEntity(world.getBot()
				.getPlayer().id, id, 0);
		connectionHandler.sendPacket(useEntityPacket);
	}
}
