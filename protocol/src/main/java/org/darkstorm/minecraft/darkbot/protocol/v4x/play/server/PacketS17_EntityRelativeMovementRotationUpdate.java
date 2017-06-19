package org.darkstorm.minecraft.darkbot.protocol.v4x.play.server;

import java.io.*;

public class PacketS17_EntityRelativeMovementRotationUpdate extends PacketS14_EntityUpdate {
	private double dx, dy, dz, yaw, pitch;

	public PacketS17_EntityRelativeMovementRotationUpdate() {
		super(0x17);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		super.readData(in);

		dx = in.readByte() / 32D;
		dy = in.readByte() / 32D;
		dz = in.readByte() / 32D;
		yaw = (in.readByte() * 360) / 256D;
		pitch = (in.readByte() * 360) / 256D;
	}

	public double getDX() {
		return dx;
	}

	public double getDY() {
		return dy;
	}

	public double getDZ() {
		return dz;
	}

	public double getYaw() {
		return yaw;
	}

	public double getPitch() {
		return pitch;
	}
}
