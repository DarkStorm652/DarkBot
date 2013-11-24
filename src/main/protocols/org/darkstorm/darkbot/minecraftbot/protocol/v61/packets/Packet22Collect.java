package org.darkstorm.darkbot.minecraftbot.protocol.v61.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

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
