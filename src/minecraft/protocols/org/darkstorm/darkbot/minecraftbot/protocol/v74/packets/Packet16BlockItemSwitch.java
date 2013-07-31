package org.darkstorm.darkbot.minecraftbot.protocol.v74.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet16BlockItemSwitch extends AbstractPacket implements
		ReadablePacket, WriteablePacket {
	public int id;

	public Packet16BlockItemSwitch() {
	}

	public Packet16BlockItemSwitch(int par1) {
		id = par1;
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		id = in.readShort();
	}

	public void writeData(DataOutputStream out) throws IOException {
		out.writeShort(id);
	}

	public int getId() {
		return 16;
	}
}
