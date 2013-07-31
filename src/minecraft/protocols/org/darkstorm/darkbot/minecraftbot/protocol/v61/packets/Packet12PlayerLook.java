package org.darkstorm.darkbot.minecraftbot.protocol.v61.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.WriteablePacket;

public class Packet12PlayerLook extends Packet10Flying implements WriteablePacket {
	public float yaw;
	public float pitch;

	public Packet12PlayerLook() {
	}

	public Packet12PlayerLook(float par1, float par2, boolean par3) {
		super(par3);
		yaw = par1;
		pitch = par2;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeFloat(yaw);
		out.writeFloat(pitch);
		super.writeData(out);
	}

	@Override
	public int getId() {
		return 12;
	}
}
