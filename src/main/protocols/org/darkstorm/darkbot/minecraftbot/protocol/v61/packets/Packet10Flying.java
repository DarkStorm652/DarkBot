package org.darkstorm.darkbot.minecraftbot.protocol.v61.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet10Flying extends AbstractPacket implements WriteablePacket {
	public boolean onGround;

	public Packet10Flying() {
	}

	public Packet10Flying(boolean par1) {
		onGround = par1;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.write(onGround ? 1 : 0);
	}

	@Override
	public int getId() {
		return 10;
	}
}
