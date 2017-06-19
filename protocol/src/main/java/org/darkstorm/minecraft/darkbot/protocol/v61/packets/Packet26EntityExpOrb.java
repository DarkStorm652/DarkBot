package org.darkstorm.minecraft.darkbot.protocol.v61.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet26EntityExpOrb extends AbstractPacket implements
		ReadablePacket {
	public int entityId;

	public int posX;
	public int posY;
	public int posZ;

	public int xpValue;

	public Packet26EntityExpOrb() {
	}

	public void readData(DataInputStream in) throws IOException {
		entityId = in.readInt();
		posX = in.readInt();
		posY = in.readInt();
		posZ = in.readInt();
		xpValue = in.readShort();
	}

	public int getId() {
		return 26;
	}
}
