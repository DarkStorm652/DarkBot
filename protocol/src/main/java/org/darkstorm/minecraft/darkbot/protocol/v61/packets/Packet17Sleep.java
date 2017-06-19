package org.darkstorm.minecraft.darkbot.protocol.v61.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet17Sleep extends AbstractPacket implements ReadablePacket {
	public int entityID;
	public int bedX;
	public int bedY;
	public int bedZ;
	public int field_22046_e;

	public Packet17Sleep() {
	}

	public void readData(DataInputStream in) throws IOException {
		entityID = in.readInt();
		field_22046_e = in.readByte();
		bedX = in.readInt();
		bedY = in.readByte();
		bedZ = in.readInt();
	}

	public int getId() {
		return 17;
	}
}
