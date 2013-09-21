package org.darkstorm.darkbot.minecraftbot.protocol.v78.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

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