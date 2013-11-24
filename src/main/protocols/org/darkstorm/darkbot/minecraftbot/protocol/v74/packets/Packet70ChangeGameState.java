package org.darkstorm.darkbot.minecraftbot.protocol.v74.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet70ChangeGameState extends AbstractPacket implements
		ReadablePacket {
	public static final String[] REASON_MESSAGES = { "tile.bed.notValid", null,
			null, "gameMode.changed" };

	public int reason;
	public int gameMode;

	public Packet70ChangeGameState() {
	}

	public void readData(DataInputStream in) throws IOException {
		reason = in.readByte();
		gameMode = in.readByte();
	}

	public int getId() {
		return 70;
	}
}
