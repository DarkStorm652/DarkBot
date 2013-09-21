package org.darkstorm.darkbot.minecraftbot.protocol.v78.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

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
