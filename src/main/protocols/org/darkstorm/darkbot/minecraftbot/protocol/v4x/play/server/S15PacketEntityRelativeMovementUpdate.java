package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

public class S15PacketEntityRelativeMovementUpdate extends S14PacketEntityUpdate {
	private double dx, dy, dz;

	public S15PacketEntityRelativeMovementUpdate() {
		super(0x15);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		super.readData(in);

		dx = in.readByte() / 32D;
		dy = in.readByte() / 32D;
		dz = in.readByte() / 32D;
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
}
