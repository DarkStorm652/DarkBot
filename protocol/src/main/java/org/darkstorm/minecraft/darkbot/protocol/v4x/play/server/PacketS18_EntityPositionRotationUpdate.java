package org.darkstorm.minecraft.darkbot.protocol.v4x.play.server;

import java.io.*;

public class PacketS18_EntityPositionRotationUpdate extends PacketS14_EntityUpdate {
	private double x, y, z, yaw, pitch;

	public PacketS18_EntityPositionRotationUpdate() {
		super(0x18);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		super.readData(in);

		x = in.readInt() / 32D;
		y = in.readInt() / 32D;
		z = in.readInt() / 32D;
		yaw = (in.readByte() * 360) / 256D;
		pitch = (in.readByte() * 360) / 256D;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public double getYaw() {
		return yaw;
	}

	public double getPitch() {
		return pitch;
	}
}
