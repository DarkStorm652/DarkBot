package org.darkstorm.minecraft.darkbot.protocol.v61.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet42RemoveEntityEffect extends AbstractPacket implements
		ReadablePacket {

	public int entityId;

	public byte effectId;

	public Packet42RemoveEntityEffect() {
	}

	public void readData(DataInputStream in) throws IOException {
		entityId = in.readInt();
		effectId = in.readByte();
	}

	public int getId() {
		return 42;
	}
}
