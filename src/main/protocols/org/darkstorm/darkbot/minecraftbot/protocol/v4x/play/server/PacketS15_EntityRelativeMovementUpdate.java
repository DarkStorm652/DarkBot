package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

public class PacketS15_EntityRelativeMovementUpdate extends PacketS14_EntityUpdate {
	private double dx, dy, dz;

	public PacketS15_EntityRelativeMovementUpdate() {
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
