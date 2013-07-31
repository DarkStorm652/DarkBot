package org.darkstorm.darkbot.minecraftbot.protocol.v74.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet55BlockDestroy extends AbstractPacket implements
		ReadablePacket {
	public int entityId;
	public int x;
	public int y;
	public int z;
	public int destroyStage;

	public Packet55BlockDestroy() {
	}

	public void readData(DataInputStream in) throws IOException {
		entityId = in.readInt();
		x = in.readInt();
		y = in.readInt();
		z = in.readInt();
		destroyStage = in.read();
	}

	@Override
	public int getId() {
		return 55;
	}
}