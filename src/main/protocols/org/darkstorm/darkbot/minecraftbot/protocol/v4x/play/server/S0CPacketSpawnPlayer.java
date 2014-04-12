package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;
import org.darkstorm.darkbot.minecraftbot.util.IntHashMap;
import org.darkstorm.darkbot.minecraftbot.world.entity.WatchableObject;

public class S0CPacketSpawnPlayer extends AbstractPacketX implements ReadablePacket {
	private int entityId;
	private String uuid, name;

	private double x, y, z, yaw, pitch;
	private int heldItemId;
	private IntHashMap<WatchableObject> metadata;

	public S0CPacketSpawnPlayer() {
		super(0x0C, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		entityId = readVarInt(in);
		uuid = readString(in);
		name = readString(in);

		x = in.readInt() / 32D;
		y = in.readInt() / 32D;
		z = in.readInt() / 32D;
		yaw = (in.readByte() * 360) / 256D;
		pitch = (in.readByte() * 360) / 256D;
		heldItemId = in.readShort();
		metadata = readWatchableObjects(in);
	}

	public int getEntityId() {
		return entityId;
	}

	public String getUuid() {
		return uuid;
	}

	public String getName() {
		return name;
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

	public int getHeldItemId() {
		return heldItemId;
	}

	public IntHashMap<WatchableObject> getMetadata() {
		return metadata;
	}
}
