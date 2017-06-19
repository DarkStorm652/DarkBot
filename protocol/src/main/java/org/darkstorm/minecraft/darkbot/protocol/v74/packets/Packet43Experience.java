package org.darkstorm.minecraft.darkbot.protocol.v74.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

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
