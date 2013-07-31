package org.darkstorm.darkbot.minecraftbot.protocol.v61.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.ReadablePacket;

public class Packet13PlayerLookMove extends Packet10Flying implements ReadablePacket {
	public double x;
	public double y;
	public double z;
	public double stance;
	public float yaw;
	public float pitch;

	public Packet13PlayerLookMove() {
		super(false);
	}

	public Packet13PlayerLookMove(double x, double y, double stance, double z, float yaw, float pitch, boolean onGround) {
		super(onGround);
		this.x = x;
		this.y = y;
		this.stance = stance;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		x = in.readDouble();
		stance = in.readDouble();
		y = in.readDouble();
		z = in.readDouble();
		yaw = in.readFloat();
		pitch = in.readFloat();
		onGround = in.readBoolean();
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeDouble(x);
		out.writeDouble(y);
		out.writeDouble(stance);
		out.writeDouble(z);
		out.writeFloat(yaw);
		out.writeFloat(pitch);
		super.writeData(out);
	}

	@Override
	public int getId() {
		return 13;
	}
}
