package org.darkstorm.darkbot.minecraftbot.protocol.v78.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet130UpdateSign extends AbstractPacket implements
		ReadablePacket, WriteablePacket {
	public int x;
	public int y;
	public int z;
	public String[] text;

	public Packet130UpdateSign() {
	}

	public Packet130UpdateSign(int x, int y, int z, String[] text) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.text = text;
	}

	public void readData(DataInputStream in) throws IOException {
		x = in.readInt();
		y = in.readShort();
		z = in.readInt();
		text = new String[4];

		for(int i = 0; i < 4; i++)
			text[i] = readString(in, 15);
	}

	public void writeData(DataOutputStream out) throws IOException {
		out.writeInt(x);
		out.writeShort(y);
		out.writeInt(z);

		for(int i = 0; i < 4; i++)
			writeString(text[i], out);
	}

	public int getId() {
		return 130;
	}
}
