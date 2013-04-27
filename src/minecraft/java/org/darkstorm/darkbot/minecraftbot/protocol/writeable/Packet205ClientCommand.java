package org.darkstorm.darkbot.minecraftbot.protocol.writeable;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet205ClientCommand extends AbstractPacket implements
		WriteablePacket {
	public int forceRespawn;

	public Packet205ClientCommand(int par1) {
		forceRespawn = par1;
	}

	public void writeData(DataOutputStream out) throws IOException {
		out.writeByte(forceRespawn & 255);
	}

	@Override
	public int getId() {
		return 205;
	}
}