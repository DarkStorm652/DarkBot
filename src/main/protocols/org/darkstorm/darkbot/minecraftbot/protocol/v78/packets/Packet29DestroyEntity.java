package org.darkstorm.darkbot.minecraftbot.protocol.v78.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

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
