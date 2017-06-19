package org.darkstorm.minecraft.darkbot.protocol.v74.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet0KeepAlive extends AbstractPacket implements ReadablePacket,
		WriteablePacket {
	public int randomId;

	public Packet0KeepAlive() {
	}

	public Packet0KeepAlive(int par1) {
		randomId = par1;
	}

	public void readData(DataInputStream in) throws IOException {
		randomId = in.readInt();
	}

	public void writeData(DataOutputStream out) throws IOException {
		out.writeInt(randomId);
	}

	@Override
	public int getId() {
		return 0;
	}
}
