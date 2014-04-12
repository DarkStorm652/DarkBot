package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.client;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class C00PacketKeepAlive extends AbstractPacketX implements WriteablePacket {
	private int pingId;

	public C00PacketKeepAlive(int pingId) {
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
