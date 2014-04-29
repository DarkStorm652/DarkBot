package org.darkstorm.darkbot.minecraftbot.protocol.v5x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketS00_KeepAlive extends AbstractPacketX implements ReadablePacket {
	private int pingId;

	public PacketS00_KeepAlive() {
		super(0x00, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		pingId = in.readInt();
	}

	public int getPingId() {
		return pingId;
	}
}
