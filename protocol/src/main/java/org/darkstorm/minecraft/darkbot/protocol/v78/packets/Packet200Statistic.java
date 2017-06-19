package org.darkstorm.minecraft.darkbot.protocol.v78.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

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
