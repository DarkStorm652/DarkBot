package org.darkstorm.darkbot.minecraftbot.protocol.v78.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet54PlayNoteBlock extends AbstractPacket implements
		ReadablePacket {
	public int xLocation;
	public int yLocation;
	public int zLocation;

	public int instrumentType;
	public int pitch;
	public int blockId;

	public Packet54PlayNoteBlock() {
	}

	public void readData(DataInputStream in) throws IOException {
		xLocation = in.readInt();
		yLocation = in.readShort();
		zLocation = in.readInt();
		instrumentType = in.read();
		pitch = in.read();
		blockId = in.readShort() & 0xfff;
	}

	public int getId() {
		return 54;
	}
}
