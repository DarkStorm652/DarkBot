package org.darkstorm.darkbot.minecraftbot.protocol.bidirectional;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet255KickDisconnect extends AbstractPacket implements
		ReadablePacket, WriteablePacket {

	public String reason;

	public Packet255KickDisconnect() {
	}

	public Packet255KickDisconnect(String par1Str) {
		reason = par1Str;
	}

	public void readData(DataInputStream in) throws IOException {
		reason = readString(in, 256);
	}

	public void writeData(DataOutputStream out) throws IOException {
		writeString(reason, out);
	}

	public int getId() {
		return 255;
	}
}
