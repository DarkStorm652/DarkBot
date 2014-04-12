package org.darkstorm.darkbot.minecraftbot.protocol.v74.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.util.IntHashMap;
import org.darkstorm.darkbot.minecraftbot.world.entity.WatchableObject;

public class Packet20NamedEntitySpawn extends AbstractPacket implements ReadablePacket {
	public int entityId;
	public String name;

	public int xPosition;
	public int yPosition;
	public int zPosition;
	public byte rotation;
	public byte pitch;

	public int currentItem;
	public IntHashMap<WatchableObject> data;

	public Packet20NamedEntitySpawn() {
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		entityId = in.readInt();
		name = readString(in, 16);
		xPosition = in.readInt();
		yPosition = in.readInt();
		zPosition = in.readInt();
		rotation = in.readByte();
		pitch = in.readByte();
		currentItem = in.readShort();
		data = readWatchableObjects(in);
	}

	@Override
	public int getId() {
		return 20;
	}
}
