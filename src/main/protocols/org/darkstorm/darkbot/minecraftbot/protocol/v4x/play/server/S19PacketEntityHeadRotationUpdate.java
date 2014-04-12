package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

public class S19PacketEntityHeadRotationUpdate extends S14PacketEntityUpdate {
	private double headYaw;

	public S19PacketEntityHeadRotationUpdate() {
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
