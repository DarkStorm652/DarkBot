package org.darkstorm.minecraft.darkbot.protocol.v74.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

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
