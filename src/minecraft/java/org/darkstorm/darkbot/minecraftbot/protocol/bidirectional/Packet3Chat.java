package org.darkstorm.darkbot.minecraftbot.protocol.bidirectional;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet3Chat extends AbstractPacket implements ReadablePacket,
		WriteablePacket {
	public static int MAX_CHAT_LENGTH = 119;

	public String message;

	public Packet3Chat() {
	}

	public Packet3Chat(String par1Str) {
		if(par1Str.length() > 119) {
			par1Str = par1Str.substring(0, 119);
		}

		message = par1Str;
	}

	public void readData(DataInputStream in) throws IOException {
		message = readString(in, Integer.MAX_VALUE);
	}

	public void writeData(DataOutputStream out) throws IOException {
		writeString(message, out);
	}

	public int getId() {
		return 3;
	}
}
