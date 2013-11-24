package org.darkstorm.darkbot.minecraftbot.protocol.v74.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

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
