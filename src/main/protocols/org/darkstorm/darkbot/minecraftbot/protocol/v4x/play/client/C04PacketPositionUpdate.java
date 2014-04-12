package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.client;

import java.io.*;

public class C04PacketPositionUpdate extends C03PacketPlayerUpdate {
	private double x, y, z, stance;

	public C04PacketPositionUpdate(double x, double y, double z, double stance, boolean grounded) {
		super(0x04, grounded);

		this.x = x;
		this.y = y;
		this.z = z;
		this.stance = stance;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeDouble(x);
		out.writeDouble(y);
		out.writeDouble(stance);
		out.writeDouble(z);

		super.writeData(out);
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

	public double getStance() {
		return stance;
	}
}
