package org.darkstorm.minecraft.darkbot.protocol.v4x.play.server;

import java.io.*;

public class PacketS19_EntityHeadRotationUpdate extends PacketS14_EntityUpdate {
	private double headYaw;

	public PacketS19_EntityHeadRotationUpdate() {
		super(0x19);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		super.readData(in);

		headYaw = (in.readByte() * 360) / 256D;
	}

	public double getHeadYaw() {
		return headYaw;
	}
}
