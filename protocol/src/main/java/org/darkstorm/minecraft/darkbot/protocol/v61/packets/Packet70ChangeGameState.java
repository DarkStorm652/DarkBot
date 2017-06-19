package org.darkstorm.minecraft.darkbot.protocol.v61.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

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
