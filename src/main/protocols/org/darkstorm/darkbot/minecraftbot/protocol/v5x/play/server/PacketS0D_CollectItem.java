package org.darkstorm.darkbot.minecraftbot.protocol.v5x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketS0D_CollectItem extends AbstractPacketX implements ReadablePacket {
	private int itemEntityId, collectorEntityId;

	public PacketS0D_CollectItem() {
		super(0x0D, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		itemEntityId = in.readInt();
		collectorEntityId = in.readInt();
	}

	public int getItemEntityId() {
		return itemEntityId;
	}

	public int getCollectorEntityId() {
		return collectorEntityId;
	}
}
