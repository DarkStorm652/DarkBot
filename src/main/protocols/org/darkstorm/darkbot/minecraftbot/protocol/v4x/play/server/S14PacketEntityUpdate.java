package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class S14PacketEntityUpdate extends AbstractPacketX implements ReadablePacket {
	private int entityId;

	public S14PacketEntityUpdate() {
		super(0x14, State.PLAY, Direction.DOWNSTREAM);
	}

	protected S14PacketEntityUpdate(int id) {
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
