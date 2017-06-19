package org.darkstorm.minecraft.darkbot.protocol.v74.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet207SetScore extends AbstractPacket implements ReadablePacket {
	private String string1 = "";
	private String string2 = "";
	private int int1 = 0;
	private int int2 = 0;

	@Override
	public void readData(DataInputStream in)
			throws IOException {
		string1 = readString(in, 16);
		int2 = in.readByte();

		if(int2 != 1) {
			string2 = readString(in, 16);
			int1 = in.readInt();
		}
	}

	@Override
	public int getId() {
		return 207;
	}

	public String getString1() {
		return string1;
	}

	public String getString2() {
		return string2;
	}

	public int getInt1() {
		return int1;
	}

	public int getInt2() {
		return int2;
	}
}
