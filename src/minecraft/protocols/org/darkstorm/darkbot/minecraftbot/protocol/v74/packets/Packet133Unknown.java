package org.darkstorm.darkbot.minecraftbot.protocol.v74.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet133Unknown extends AbstractPacket implements ReadablePacket {
	public byte byte1;
	public int int1, int2, int3;

	public Packet133Unknown() {
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		byte1 = in.readByte();
		int1 = in.readInt();
		int2 = in.readInt();
		int3 = in.readInt();
	}

	@Override
	public int getId() {
		return 133;
	}
}
