package org.darkstorm.darkbot.minecraftbot.protocol.v61.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet43Experience extends AbstractPacket implements
		ReadablePacket {
	public float experience;
	public int experienceTotal;
	public int experienceLevel;

	public Packet43Experience() {
	}

	public void readData(DataInputStream in) throws IOException {
		experience = in.readFloat();
		experienceLevel = in.readShort();
		experienceTotal = in.readShort();
	}

	public int getId() {
		return 43;
	}
}
