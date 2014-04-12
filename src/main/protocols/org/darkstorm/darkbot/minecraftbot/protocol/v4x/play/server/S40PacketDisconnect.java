package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class S40PacketDisconnect extends AbstractPacketX implements ReadablePacket {
	private String reason;

	public S40PacketDisconnect() {
		super(0x40, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		reason = readString(in);
	}

	public String getReason() {
		return reason;
	}
}
