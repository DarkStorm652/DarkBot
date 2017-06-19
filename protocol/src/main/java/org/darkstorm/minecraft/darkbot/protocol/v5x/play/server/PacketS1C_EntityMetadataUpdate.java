package org.darkstorm.minecraft.darkbot.protocol.v5x.play.server;

import org.darkstorm.minecraft.darkbot.util.IntHashMap;
import org.darkstorm.minecraft.darkbot.world.entity.WatchableObject;

import java.io.*;

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
