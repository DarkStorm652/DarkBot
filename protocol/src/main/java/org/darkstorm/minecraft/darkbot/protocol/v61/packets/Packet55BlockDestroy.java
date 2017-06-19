package org.darkstorm.minecraft.darkbot.protocol.v61.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

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