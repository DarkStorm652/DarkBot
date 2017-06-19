package org.darkstorm.minecraft.darkbot.protocol.v4x.play.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

public class PacketS0E_SpawnObject extends AbstractPacketX implements ReadablePacket {
	private int entityId, type;
	private double x, y, z, yaw, pitch;

	private int data;
	private double velocityX, velocityY, velocityZ;

	public PacketS0E_SpawnObject() {
		super(0x0E, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		entityId = readVarInt(in);
		type = in.read();

		x = in.readInt() / 32D;
		y = in.readInt() / 32D;
		z = in.readInt() / 32D;
		yaw = (in.readByte() * 360) / 256D;
		pitch = (in.readByte() * 360) / 256D;

		data = in.readInt();
		if(data != 0) {
			velocityX = in.readShort() / 8000D;
			velocityY = in.readShort() / 8000D;
			velocityZ = in.readShort() / 8000D;
		}
	}

	public int getEntityId() {
		return entityId;
	}

	public int getType() {
		return type;
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

	public int getData() {
		return data;
	}

	public double getVelocityX() {
		return velocityX;
	}

	public double getVelocityY() {
		return velocityY;
	}

	public double getVelocityZ() {
		return velocityZ;
	}
}
