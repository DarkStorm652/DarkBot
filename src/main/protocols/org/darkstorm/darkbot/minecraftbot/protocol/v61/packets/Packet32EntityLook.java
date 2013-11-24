package org.darkstorm.darkbot.minecraftbot.protocol.v61.packets;

import java.io.*;

public class Packet32EntityLook extends Packet30Entity {
	public Packet32EntityLook() {
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		super.readData(in);
		yaw = in.readByte();
		pitch = in.readByte();
	}

	@Override
	public int getId() {
		return 32;
	}
}
