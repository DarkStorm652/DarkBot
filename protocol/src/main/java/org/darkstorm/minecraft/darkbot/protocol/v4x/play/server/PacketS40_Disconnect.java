package org.darkstorm.minecraft.darkbot.protocol.v4x.play.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

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
