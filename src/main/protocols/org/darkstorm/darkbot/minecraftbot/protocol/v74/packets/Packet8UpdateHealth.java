package org.darkstorm.darkbot.minecraftbot.protocol.v74.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet8UpdateHealth extends AbstractPacket implements ReadablePacket {
	public float healthMP;
	public int food;

	public float foodSaturation;

	public Packet8UpdateHealth() {
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		healthMP = in.readFloat();
		food = in.readShort();
		foodSaturation = in.readFloat();
	}

	@Override
	public int getId() {
		return 8;
	}
}
