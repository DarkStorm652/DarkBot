package org.darkstorm.darkbot.minecraftbot.protocol.v5x.login.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketLS00_Disconnect extends AbstractPacketX implements ReadablePacket {
	private String data;

	public PacketLS00_Disconnect() {
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
