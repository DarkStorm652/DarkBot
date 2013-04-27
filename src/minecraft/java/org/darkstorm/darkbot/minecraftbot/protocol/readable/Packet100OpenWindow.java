package org.darkstorm.darkbot.minecraftbot.protocol.readable;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet100OpenWindow extends AbstractPacket implements
		ReadablePacket {
	public int windowId;
	public int inventoryType;
	public String windowTitle;
	public int slotsCount;

	public Packet100OpenWindow() {
	}

	public void readData(DataInputStream in) throws IOException {
		windowId = in.readByte() & 0xff;
		inventoryType = in.readByte() & 0xff;
		windowTitle = readString(in, 32);
		slotsCount = in.readByte() & 0xff;
	}

	@Override
	public int getId() {
		return 100;
	}
}
