package org.darkstorm.darkbot.minecraftbot.protocol.readable;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet200Statistic extends AbstractPacket implements
		ReadablePacket {
	public int statisticId;
	public int amount;

	public Packet200Statistic() {
	}

	public void readData(DataInputStream in) throws IOException {
		statisticId = in.readInt();
		amount = in.readByte();
	}

	public int getId() {
		return 200;
	}
}
