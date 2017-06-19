package org.darkstorm.minecraft.darkbot.protocol.v5x.play.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

public class PacketS14_EntityUpdate extends AbstractPacketX implements ReadablePacket {
	private int entityId;

	public PacketS14_EntityUpdate() {
		super(0x14, State.PLAY, Direction.DOWNSTREAM);
	}

	protected PacketS14_EntityUpdate(int id) {
		super(id, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		entityId = in.readInt();
	}

	public int getEntityId() {
		return entityId;
	}
}
