package org.darkstorm.darkbot.minecraftbot.protocol.v78.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet250CustomPayload extends AbstractPacket implements
		ReadablePacket, WriteablePacket {

	public String channel;

	public int length;
	public byte data[];

	public Packet250CustomPayload() {
	}

	public void readData(DataInputStream in) throws IOException {
		channel = readString(in, 16);
		length = in.readShort();

		if(length > 0 && length < 32767) {
			data = new byte[length];
			in.readFully(data);
		}
	}

	public void writeData(DataOutputStream out) throws IOException {
		writeString(channel, out);
		out.writeShort((short) length);

		if(data != null) {
			out.write(data);
		}
	}

	public int getId() {
		return 250;
	}
}
