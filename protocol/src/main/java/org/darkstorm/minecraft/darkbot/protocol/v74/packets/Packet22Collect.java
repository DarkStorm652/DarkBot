package org.darkstorm.minecraft.darkbot.protocol.v74.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet22Collect extends AbstractPacket implements ReadablePacket {
	public int collectedEntityId;
	public int collectorEntityId;

	public Packet22Collect() {
	}

	public void readData(DataInputStream in) throws IOException {
		collectedEntityId = in.readInt();
		collectorEntityId = in.readInt();
	}

	public int getId() {
		return 22;
	}
}
