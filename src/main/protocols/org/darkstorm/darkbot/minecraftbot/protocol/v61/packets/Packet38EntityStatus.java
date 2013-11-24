package org.darkstorm.darkbot.minecraftbot.protocol.v61.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet38EntityStatus extends AbstractPacket implements
		ReadablePacket {
	public int entityId;
	public byte entityStatus;

	public Packet38EntityStatus() {
	}

	public void readData(DataInputStream in) throws IOException {
		entityId = in.readInt();
		entityStatus = in.readByte();
	}

	public int getId() {
		return 38;
	}
}
