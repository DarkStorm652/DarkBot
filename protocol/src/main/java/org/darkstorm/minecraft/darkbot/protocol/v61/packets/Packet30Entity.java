package org.darkstorm.minecraft.darkbot.protocol.v61.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet30Entity extends AbstractPacket implements ReadablePacket {
	public int entityId;

	public byte xPosition;
	public byte yPosition;
	public byte zPosition;
	public byte yaw;
	public byte pitch;

	public Packet30Entity() {
	}

	public void readData(DataInputStream in) throws IOException {
		entityId = in.readInt();
	}

	public int getId() {
		return 30;
	}
}
