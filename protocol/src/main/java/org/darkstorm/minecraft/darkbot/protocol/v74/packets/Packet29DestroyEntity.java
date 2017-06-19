package org.darkstorm.minecraft.darkbot.protocol.v74.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet29DestroyEntity extends AbstractPacket implements
		ReadablePacket {
	public int[] entityIds;

	public Packet29DestroyEntity() {
	}

	public void readData(DataInputStream in) throws IOException {
		entityIds = new int[in.readByte()];

		for(int var2 = 0; var2 < entityIds.length; var2++)
			entityIds[var2] = in.readInt();
	}

	public int getId() {
		return 29;
	}
}
