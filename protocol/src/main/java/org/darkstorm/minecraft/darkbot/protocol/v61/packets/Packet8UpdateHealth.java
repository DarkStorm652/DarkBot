package org.darkstorm.minecraft.darkbot.protocol.v61.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

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
