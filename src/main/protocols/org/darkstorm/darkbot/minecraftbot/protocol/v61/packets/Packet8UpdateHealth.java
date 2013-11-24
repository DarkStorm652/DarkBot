package org.darkstorm.darkbot.minecraftbot.protocol.v61.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet8UpdateHealth extends AbstractPacket implements
		ReadablePacket {
	public int healthMP;
	public int food;

	public float foodSaturation;

	public Packet8UpdateHealth() {
	}

	public void readData(DataInputStream in) throws IOException {
		healthMP = in.readShort();
		food = in.readShort();
		foodSaturation = in.readFloat();
	}

	public int getId() {
		return 8;
	}
}
