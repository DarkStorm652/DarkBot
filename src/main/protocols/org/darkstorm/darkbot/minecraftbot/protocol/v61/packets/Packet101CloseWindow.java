package org.darkstorm.darkbot.minecraftbot.protocol.v61.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet101CloseWindow extends AbstractPacket implements
		ReadablePacket, WriteablePacket {
	public int windowId;

	public Packet101CloseWindow() {
	}

	public Packet101CloseWindow(int par1) {
		windowId = par1;
	}

	public void readData(DataInputStream in) throws IOException {
		windowId = in.readByte();
	}

	public void writeData(DataOutputStream out) throws IOException {
		out.writeByte(windowId);
	}

	@Override
	public int getId() {
		return 101;
	}
}
