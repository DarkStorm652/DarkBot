package org.darkstorm.darkbot.minecraftbot.protocol.v78.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

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
