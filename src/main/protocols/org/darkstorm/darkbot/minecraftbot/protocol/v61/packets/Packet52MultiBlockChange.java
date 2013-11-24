package org.darkstorm.darkbot.minecraftbot.protocol.v61.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet52MultiBlockChange extends AbstractPacket implements
		ReadablePacket {
	public int xPosition;
	public int zPosition;

	public byte[] metadataArray;
	public int size;

	public Packet52MultiBlockChange() {
	}

	public void readData(DataInputStream in) throws IOException {
		xPosition = in.readInt();
		zPosition = in.readInt();
		size = in.readShort() & 0xffff;
		int i = in.readInt();

		if(i > 0) {
			metadataArray = new byte[i];
			in.readFully(metadataArray);
		}
	}

	public int getId() {
		return 52;
	}
}
