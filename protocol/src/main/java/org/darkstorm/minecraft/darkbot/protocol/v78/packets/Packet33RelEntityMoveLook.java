package org.darkstorm.minecraft.darkbot.protocol.v78.packets;

import java.io.*;

public class Packet33RelEntityMoveLook extends Packet30Entity {
	public Packet33RelEntityMoveLook() {
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		super.readData(in);
		xPosition = in.readByte();
		yPosition = in.readByte();
		zPosition = in.readByte();
		yaw = in.readByte();
		pitch = in.readByte();
	}

	@Override
	public int getId() {
		return 33;
	}
}
