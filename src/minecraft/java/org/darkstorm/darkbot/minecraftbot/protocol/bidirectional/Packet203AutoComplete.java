package org.darkstorm.darkbot.minecraftbot.protocol.bidirectional;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet203AutoComplete extends AbstractPacket implements
		ReadablePacket, WriteablePacket {

	public String text;

	public Packet203AutoComplete() {
	}

	public Packet203AutoComplete(String par1Str) {
		text = par1Str;
	}

	public void readData(DataInputStream in) throws IOException {
		text = readString(in, Packet3Chat.MAX_CHAT_LENGTH);
	}

	public void writeData(DataOutputStream out) throws IOException {
		writeString(text, out);
	}

	@Override
	public int getId() {
		return 203;
	}
}