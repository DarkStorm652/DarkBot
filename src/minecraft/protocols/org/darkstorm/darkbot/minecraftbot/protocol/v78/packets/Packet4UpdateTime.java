package org.darkstorm.darkbot.minecraftbot.protocol.v78.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet4UpdateTime extends AbstractPacket implements ReadablePacket {
	public long time;
	public long otherTime;

	public Packet4UpdateTime() {
	}

	public void readData(DataInputStream in) throws IOException {
		otherTime = in.readLong();
		time = in.readLong();
	}

	public int getId() {
		return 4;
	}
}
