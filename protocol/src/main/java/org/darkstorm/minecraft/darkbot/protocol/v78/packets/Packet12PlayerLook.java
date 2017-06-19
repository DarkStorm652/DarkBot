package org.darkstorm.minecraft.darkbot.protocol.v78.packets;

import org.darkstorm.minecraft.darkbot.protocol.WriteablePacket;

import java.io.*;

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
