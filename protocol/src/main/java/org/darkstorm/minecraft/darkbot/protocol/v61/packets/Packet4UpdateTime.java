package org.darkstorm.minecraft.darkbot.protocol.v61.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

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
