package org.darkstorm.minecraft.darkbot.protocol.v4x.play.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

public class PacketS08_Teleport extends AbstractPacketX implements ReadablePacket {
	private double x, y, z, yaw, pitch;
	private boolean grounded;

	public PacketS08_Teleport() {
		super(0x08, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		x = in.readDouble();
		y = in.readDouble();
		z = in.readDouble();
		yaw = in.readFloat();
		pitch = in.readFloat();
		grounded = in.readBoolean();
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

	public boolean isGrounded() {
		return grounded;
	}
}
