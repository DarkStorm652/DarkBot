package org.darkstorm.minecraft.darkbot.protocol.v61.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet39AttachEntity extends AbstractPacket implements
		ReadablePacket {
	public int entityId;
	public int vehicleEntityId;

	public Packet39AttachEntity() {
	}

	public void readData(DataInputStream in) throws IOException {
		entityId = in.readInt();
		vehicleEntityId = in.readInt();
	}

	public int getId() {
		return 39;
	}
}
