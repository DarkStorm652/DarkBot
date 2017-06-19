package org.darkstorm.minecraft.darkbot.protocol.v78.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

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
