package org.darkstorm.darkbot.minecraftbot.protocol.v5x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketS40_Disconnect extends AbstractPacketX implements ReadablePacket {
	private String reason;

	public PacketS40_Disconnect() {
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
