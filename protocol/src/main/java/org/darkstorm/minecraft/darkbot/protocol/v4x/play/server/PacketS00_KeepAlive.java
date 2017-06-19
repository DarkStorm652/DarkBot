package org.darkstorm.minecraft.darkbot.protocol.v4x.play.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

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
