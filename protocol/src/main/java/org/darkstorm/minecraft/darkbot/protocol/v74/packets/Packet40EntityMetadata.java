package org.darkstorm.minecraft.darkbot.protocol.v74.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.util.IntHashMap;
import org.darkstorm.minecraft.darkbot.world.entity.WatchableObject;

import java.io.*;

public class Packet40EntityMetadata extends AbstractPacket implements ReadablePacket {
	public int entityId;
	public IntHashMap<WatchableObject> metadata;

	public Packet40EntityMetadata() {
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		entityId = in.readInt();
		metadata = readWatchableObjects(in);
	}

	@Override
	public int getId() {
		return 40;
	}
}
