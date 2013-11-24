package org.darkstorm.darkbot.minecraftbot.protocol.v61.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet35EntityHeadRotation extends AbstractPacket implements
		ReadablePacket {
	public int entityId;
	public byte headRotationYaw;

	public Packet35EntityHeadRotation() {
	}

	public void readData(DataInputStream in) throws IOException {
		entityId = in.readInt();
		headRotationYaw = in.readByte();
	}

	public int getId() {
		return 35;
	}
}
