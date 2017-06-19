package org.darkstorm.minecraft.darkbot.protocol.v61.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet34EntityTeleport extends AbstractPacket implements
		ReadablePacket {
	public int entityId;

	public int xPosition;
	public int yPosition;
	public int zPosition;
	public byte yaw;
	public byte pitch;

	public Packet34EntityTeleport() {
	}

	public void readData(DataInputStream in) throws IOException {
		entityId = in.readInt();
		xPosition = in.readInt();
		yPosition = in.readInt();
		zPosition = in.readInt();
		yaw = (byte) in.read();
		pitch = (byte) in.read();
	}

	public int getId() {
		return 34;
	}
}
