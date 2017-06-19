package org.darkstorm.minecraft.darkbot.protocol.v5x.play.client;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

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
