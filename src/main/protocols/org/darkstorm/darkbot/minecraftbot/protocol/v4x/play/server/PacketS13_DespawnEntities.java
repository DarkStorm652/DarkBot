package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketS13_DespawnEntities extends AbstractPacketX implements ReadablePacket {
	private int[] entityIds;

	public PacketS13_DespawnEntities() {
		super(0x13, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		int length = in.read();
		int[] entityIds = new int[length];
		for(int i = 0; i < length; i++)
			entityIds[i] = in.readInt();
		this.entityIds = entityIds;
	}

	public int[] getEntityIds() {
		return entityIds.clone();
	}
}
