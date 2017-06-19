package org.darkstorm.minecraft.darkbot.protocol.v4x.play.server;

import java.io.*;

public class PacketS16_EntityRotationUpdate extends PacketS14_EntityUpdate {
	private double yaw, pitch;

	public PacketS16_EntityRotationUpdate() {
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
