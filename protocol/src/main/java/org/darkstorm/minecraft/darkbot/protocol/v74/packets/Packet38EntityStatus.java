package org.darkstorm.minecraft.darkbot.protocol.v74.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

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
