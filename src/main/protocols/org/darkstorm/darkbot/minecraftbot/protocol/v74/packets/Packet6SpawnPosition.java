package org.darkstorm.darkbot.minecraftbot.protocol.v74.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet6SpawnPosition extends AbstractPacket implements
		ReadablePacket {
	public int xPosition;
	public int yPosition;
	public int zPosition;

	public Packet6SpawnPosition() {
	}

	public void readData(DataInputStream in) throws IOException {
		xPosition = in.readInt();
		yPosition = in.readInt();
		zPosition = in.readInt();
	}

	public int getId() {
		return 6;
	}
}
