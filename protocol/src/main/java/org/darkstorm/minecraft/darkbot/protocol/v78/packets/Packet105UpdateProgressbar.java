package org.darkstorm.minecraft.darkbot.protocol.v78.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

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
