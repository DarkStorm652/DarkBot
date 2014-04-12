package org.darkstorm.darkbot.minecraftbot.protocol.v4x.login.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class LS00PacketDisconnect extends AbstractPacketX implements ReadablePacket {
	private String data;

	public LS00PacketDisconnect() {
		super(0x00, State.LOGIN, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		data = readString(in);
	}

	public String getData() {
		return data;
	}
}
