package org.darkstorm.darkbot.minecraftbot.protocol.v74.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet105UpdateProgressbar extends AbstractPacket implements
		ReadablePacket {
	public int windowId;
	public int progressBar;
	public int progressBarValue;

	public Packet105UpdateProgressbar() {
	}

	public void readData(DataInputStream in) throws IOException {
		windowId = in.readByte();
		progressBar = in.readShort();
		progressBarValue = in.readShort();
	}

	public int getId() {
		return 105;
	}
}
