package org.darkstorm.minecraft.darkbot.protocol.v74.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet39AttachEntity extends AbstractPacket implements ReadablePacket {
	public int entityId;
	public int vehicleEntityId;
	public boolean leashed;

	public Packet39AttachEntity() {
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		entityId = in.readInt();
		vehicleEntityId = in.readInt();
		leashed = in.read() == 1;
	}

	@Override
	public int getId() {
		return 39;
	}
}
