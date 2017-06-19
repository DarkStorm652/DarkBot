package org.darkstorm.minecraft.darkbot.protocol.v74.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet206SetObjective extends AbstractPacket implements
		ReadablePacket {
	private String string1;
	private String string2;
	private int byte1;

	@Override
	public void readData(DataInputStream in) throws IOException {
		string1 = readString(in, 16);
		string2 = readString(in, 32);
		byte1 = in.readByte();
	}

	@Override
	public int getId() {
		return 206;
	}

	public String getString1() {
		return string1;
	}

	public String getString2() {
		return string2;
	}

	public int getByte1() {
		return byte1;
	}
}
