package org.darkstorm.minecraft.darkbot.protocol.v78.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet201PlayerInfo extends AbstractPacket implements
		ReadablePacket {

	public String playerName;

	public boolean isConnected;
	public int ping;

	public Packet201PlayerInfo() {
	}

	public void readData(DataInputStream in) throws IOException {
		playerName = readString(in, 16);
		isConnected = in.readByte() != 0;
		ping = in.readShort();
	}

	public int getId() {
		return 201;
	}
}
