package org.darkstorm.darkbot.minecraftbot.protocol.v61.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet28EntityVelocity extends AbstractPacket implements
		ReadablePacket {
	public int entityId;
	public int motionX;
	public int motionY;
	public int motionZ;

	public Packet28EntityVelocity() {
	}

	public void readData(DataInputStream in) throws IOException {
		entityId = in.readInt();
		motionX = in.readShort();
		motionY = in.readShort();
		motionZ = in.readShort();
	}

	public int getId() {
		return 28;
	}
}
