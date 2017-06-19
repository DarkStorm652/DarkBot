package org.darkstorm.minecraft.darkbot.protocol.v61.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

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
