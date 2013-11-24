package org.darkstorm.darkbot.minecraftbot.protocol.v61.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet208SetDisplayObjective extends AbstractPacket implements
		ReadablePacket {
	private int int1;
	private String string1;

	@Override
	public void readData(DataInputStream in) throws IOException {
		int1 = in.readByte();
		string1 = readString(in, 16);
	}

	@Override
	public int getId() {
		return 208;
	}

	public int getInt1() {
		return int1;
	}

	public String getString1() {
		return string1;
	}
}
