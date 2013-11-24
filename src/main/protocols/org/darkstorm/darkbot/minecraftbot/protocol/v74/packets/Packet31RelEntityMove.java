package org.darkstorm.darkbot.minecraftbot.protocol.v74.packets;

import java.io.*;

public class Packet31RelEntityMove extends Packet30Entity {
	public Packet31RelEntityMove() {
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		super.readData(in);
		xPosition = in.readByte();
		yPosition = in.readByte();
		zPosition = in.readByte();
	}

	@Override
	public int getId() {
		return 31;
	}
}
