package org.darkstorm.minecraft.darkbot.protocol.v78.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.util.IntHashMap;
import org.darkstorm.minecraft.darkbot.world.entity.WatchableObject;

import java.io.*;

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
