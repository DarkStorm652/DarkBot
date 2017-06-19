package org.darkstorm.minecraft.darkbot.protocol.v74.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

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
