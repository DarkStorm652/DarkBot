package org.darkstorm.darkbot.minecraftbot.protocol.v74.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

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
