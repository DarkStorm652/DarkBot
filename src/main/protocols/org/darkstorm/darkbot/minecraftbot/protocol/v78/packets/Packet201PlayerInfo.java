package org.darkstorm.darkbot.minecraftbot.protocol.v78.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

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
