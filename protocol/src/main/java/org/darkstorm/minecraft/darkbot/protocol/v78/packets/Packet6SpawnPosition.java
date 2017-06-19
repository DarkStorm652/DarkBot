package org.darkstorm.minecraft.darkbot.protocol.v78.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

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
