package org.darkstorm.darkbot.minecraftbot.protocol.v61.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet3Chat extends AbstractPacket implements ReadablePacket, WriteablePacket {
	public static final int MAX_CHAT_LENGTH = 100;

	public String message;

	public Packet3Chat() {
	}

	public Packet3Chat(String par1Str) {
		if(par1Str.length() > MAX_CHAT_LENGTH)
			par1Str = par1Str.substring(0, MAX_CHAT_LENGTH);

		message = par1Str;
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		message = readString(in, Integer.MAX_VALUE);
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		writeString(message, out);
	}

	@Override
	public int getId() {
		return 3;
	}
}
