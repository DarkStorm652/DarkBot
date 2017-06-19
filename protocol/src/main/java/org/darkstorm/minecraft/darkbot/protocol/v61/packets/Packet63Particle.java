package org.darkstorm.minecraft.darkbot.protocol.v61.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet63Particle extends AbstractPacket implements ReadablePacket {
	public String particleName;

	public float posX, posY, posZ;
	public float offsetX, offsetY, offsetZ;

	public float speed;
	public int quantity;

	public Packet63Particle() {
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		particleName = readString(in, 64);
		posX = in.readFloat();
		posY = in.readFloat();
		posZ = in.readFloat();
		offsetX = in.readFloat();
		offsetY = in.readFloat();
		offsetZ = in.readFloat();
		speed = in.readFloat();
		quantity = in.readInt();
	}

	@Override
	public int getId() {
		return 63;
	}
}
