package org.darkstorm.darkbot.minecraftbot.protocol.readable;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

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
