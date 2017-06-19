package org.darkstorm.minecraft.darkbot.protocol.v78.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet131MapData extends AbstractPacket implements ReadablePacket {
	public short itemId;
	public short uniqueId;
	public byte[] itemData;

	public Packet131MapData() {
	}

	public void readData(DataInputStream in) throws IOException {
		itemId = in.readShort();
		uniqueId = in.readShort();
		itemData = new byte[in.readShort()];
		in.readFully(itemData);
	}

	public int getId() {
		return 131;
	}
}
