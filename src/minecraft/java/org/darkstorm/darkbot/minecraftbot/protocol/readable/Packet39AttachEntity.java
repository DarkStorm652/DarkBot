package org.darkstorm.darkbot.minecraftbot.protocol.readable;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

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
