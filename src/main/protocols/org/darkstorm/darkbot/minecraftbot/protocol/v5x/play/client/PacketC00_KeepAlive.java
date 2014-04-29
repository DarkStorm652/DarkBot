package org.darkstorm.darkbot.minecraftbot.protocol.v5x.play.client;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketC00_KeepAlive extends AbstractPacketX implements WriteablePacket {
	private int pingId;

	public PacketC00_KeepAlive(int pingId) {
		super(0x00, State.PLAY, Direction.UPSTREAM);

		this.pingId = pingId;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeInt(pingId);
	}

	public int getPingId() {
		return pingId;
	}
}
