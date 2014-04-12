package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

public class S16PacketEntityRotationUpdate extends S14PacketEntityUpdate {
	private double yaw, pitch;

	public S16PacketEntityRotationUpdate() {
		super(0x16);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		super.readData(in);

		yaw = (in.readByte() * 360) / 256D;
		pitch = (in.readByte() * 360) / 256D;
	}

	public double getYaw() {
		return yaw;
	}

	public double getPitch() {
		return pitch;
	}
}
