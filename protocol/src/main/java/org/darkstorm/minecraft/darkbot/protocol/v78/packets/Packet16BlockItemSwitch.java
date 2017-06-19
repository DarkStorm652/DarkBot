package org.darkstorm.minecraft.darkbot.protocol.v78.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

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
