package org.darkstorm.darkbot.minecraftbot.protocol.v74.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet71Weather extends AbstractPacket implements ReadablePacket {
	public int entityID;

	public int posX;
	public int posY;
	public int posZ;

	public int isLightningBolt;

	public Packet71Weather() {
	}

	public void readData(DataInputStream in) throws IOException {
		entityID = in.readInt();
		isLightningBolt = in.readByte();
		posX = in.readInt();
		posY = in.readInt();
		posZ = in.readInt();
	}

	public int getId() {
		return 71;
	}
}
