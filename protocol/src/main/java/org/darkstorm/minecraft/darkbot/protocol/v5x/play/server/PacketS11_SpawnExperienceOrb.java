package org.darkstorm.minecraft.darkbot.protocol.v5x.play.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

public class PacketS11_SpawnExperienceOrb extends AbstractPacketX implements ReadablePacket {
	private int entityId;
	private double x, y, z;
	private int count;

	public PacketS11_SpawnExperienceOrb() {
		super(0x11, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		entityId = readVarInt(in);

		x = in.readInt() / 32D;
		y = in.readInt() / 32D;
		z = in.readInt() / 32D;
		count = in.readShort();
	}

	public int getEntityId() {
		return entityId;
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

	public int getCount() {
		return count;
	}
}
