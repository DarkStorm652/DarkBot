package org.darkstorm.minecraft.darkbot.protocol.v61.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

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
