package org.darkstorm.minecraft.darkbot.protocol.v4x.login.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

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
