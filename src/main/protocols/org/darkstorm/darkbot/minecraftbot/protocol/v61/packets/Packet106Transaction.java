package org.darkstorm.darkbot.minecraftbot.protocol.v61.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet106Transaction extends AbstractPacket implements
		ReadablePacket, WriteablePacket {
	public int windowId;
	public short shortWindowId;
	public boolean accepted;

	public Packet106Transaction() {
	}

	public Packet106Transaction(int par1, short par2, boolean par3) {
		windowId = par1;
		shortWindowId = par2;
		accepted = par3;
	}

	public void readData(DataInputStream in) throws IOException {
		windowId = in.readByte();
		shortWindowId = in.readShort();
		accepted = in.readByte() != 0;
	}

	public void writeData(DataOutputStream out) throws IOException {
		out.writeByte(windowId);
		out.writeShort(shortWindowId);
		out.writeByte(accepted ? 1 : 0);
	}

	public int getId() {
		return 106;
	}
}
