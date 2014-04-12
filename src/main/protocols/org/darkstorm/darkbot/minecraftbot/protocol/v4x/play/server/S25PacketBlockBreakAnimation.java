package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class S25PacketBlockBreakAnimation extends AbstractPacketX implements ReadablePacket {
	private int entityId, x, y, z, destroyStage;

	public S25PacketBlockBreakAnimation() {
		super(0x25, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		entityId = readVarInt(in);
		x = in.readInt();
		y = in.readInt();
		z = in.readInt();
		destroyStage = in.read();
	}

	public int getEntityId() {
		return entityId;
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

	public int getDestroyStage() {
		return destroyStage;
	}
}
