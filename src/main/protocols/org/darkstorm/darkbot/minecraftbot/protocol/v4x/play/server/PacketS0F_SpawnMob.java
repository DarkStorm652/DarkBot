package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;
import org.darkstorm.darkbot.minecraftbot.util.IntHashMap;
import org.darkstorm.darkbot.minecraftbot.world.entity.WatchableObject;

public class PacketS0F_SpawnMob extends AbstractPacketX implements ReadablePacket {
	private int entityId, type;
	private double x, y, z, yaw, pitch, headYaw;
	private double velocityX, velocityY, velocityZ;
	private IntHashMap<WatchableObject> metadata;

	public PacketS0F_SpawnMob() {
		super(0x0F, State.PLAY, Direction.DOWNSTREAM);
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
		headYaw = (in.readByte() * 360) / 256D;

		velocityX = in.readShort() / 8000D;
		velocityY = in.readShort() / 8000D;
		velocityZ = in.readShort() / 8000D;

		metadata = readWatchableObjects(in);
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

	public double getHeadYaw() {
		return headYaw;
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

	public IntHashMap<WatchableObject> getMetadata() {
		return metadata;
	}
}
