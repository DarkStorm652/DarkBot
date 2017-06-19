package org.darkstorm.minecraft.darkbot.protocol.v78.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

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