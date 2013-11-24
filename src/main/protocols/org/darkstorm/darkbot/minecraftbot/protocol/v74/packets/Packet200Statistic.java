package org.darkstorm.darkbot.minecraftbot.protocol.v74.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet200Statistic extends AbstractPacket implements ReadablePacket {
	public int statisticId;
	public int amount;

	public Packet200Statistic() {
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		statisticId = in.readInt();
		amount = in.readInt();
	}

	@Override
	public int getId() {
		return 200;
	}
}
