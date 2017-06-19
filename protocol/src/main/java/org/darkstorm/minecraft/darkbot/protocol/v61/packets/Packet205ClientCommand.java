package org.darkstorm.minecraft.darkbot.protocol.v61.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet205ClientCommand extends AbstractPacket implements WriteablePacket {
	public int forceRespawn;

	public Packet205ClientCommand() {
	}

	public Packet205ClientCommand(int par1) {
		forceRespawn = par1;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeByte(forceRespawn & 255);
	}

	@Override
	public int getId() {
		return 205;
	}
}