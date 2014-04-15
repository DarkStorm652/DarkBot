package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.util.IntHashMap;
import org.darkstorm.darkbot.minecraftbot.world.entity.WatchableObject;

public class PacketS1C_EntityMetadataUpdate extends PacketS14_EntityUpdate {
	private IntHashMap<WatchableObject> metadata;

	public PacketS1C_EntityMetadataUpdate() {
		super(0x1C);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		super.readData(in);

		metadata = readWatchableObjects(in);
	}

	public IntHashMap<WatchableObject> getMetadata() {
		return metadata;
	}
}
