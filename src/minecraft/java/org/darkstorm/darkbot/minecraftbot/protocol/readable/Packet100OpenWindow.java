package org.darkstorm.darkbot.minecraftbot.protocol.readable;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet100OpenWindow extends AbstractPacket implements
		ReadablePacket {
	public int windowId;
	public int inventoryType;
	public String windowTitle;
	public int slotsCount;
	public boolean flag;

	public Packet100OpenWindow() {
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		windowId = in.readByte() & 255;
		inventoryType = in.readByte() & 255;
		windowTitle = readString(in, 32);
		slotsCount = in.readByte() & 255;
		flag = in.readBoolean();
	}

	@Override
	public int getId() {
		return 100;
	}
}
