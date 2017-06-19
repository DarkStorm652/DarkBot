package org.darkstorm.minecraft.darkbot.protocol.v61.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet61DoorChange extends AbstractPacket implements
		ReadablePacket {
	public int sfxID;
	public int auxData;
	public int posX;
	public int posY;
	public int posZ;
	public boolean bool;

	public Packet61DoorChange() {
	}

	public void readData(DataInputStream in) throws IOException {
		sfxID = in.readInt();
		posX = in.readInt();
		posY = in.readByte() & 0xff;
		posZ = in.readInt();
		auxData = in.readInt();
		bool = in.readBoolean();
	}

	public int getId() {
		return 61;
	}
}
