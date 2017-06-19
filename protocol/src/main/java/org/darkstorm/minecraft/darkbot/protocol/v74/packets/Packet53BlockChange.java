package org.darkstorm.minecraft.darkbot.protocol.v74.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet53BlockChange extends AbstractPacket implements
		ReadablePacket {
	public int xPosition;
	public int yPosition;
	public int zPosition;

	public int type;
	public int metadata;

	public Packet53BlockChange() {
	}

	public void readData(DataInputStream in) throws IOException {
		xPosition = in.readInt();
		yPosition = in.read();
		zPosition = in.readInt();
		type = in.readShort();
		metadata = in.read();
	}

	public int getId() {
		return 53;
	}
}
