package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketS10_SpawnPainting extends AbstractPacketX implements ReadablePacket {
	private int entityId;
	private String title;
	private int x, y, z, face;

	public PacketS10_SpawnPainting() {
		super(0x10, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		entityId = readVarInt(in);
		title = readString(in);

		x = in.readInt();
		y = in.readInt();
		z = in.readInt();
		face = in.readInt();
	}

	public int getEntityId() {
		return entityId;
	}

	public String getTitle() {
		return title;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public int getFace() {
		return face;
	}
}
