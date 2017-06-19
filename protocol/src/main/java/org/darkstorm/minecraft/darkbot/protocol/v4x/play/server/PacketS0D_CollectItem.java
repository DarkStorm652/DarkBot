package org.darkstorm.minecraft.darkbot.protocol.v4x.play.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

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
