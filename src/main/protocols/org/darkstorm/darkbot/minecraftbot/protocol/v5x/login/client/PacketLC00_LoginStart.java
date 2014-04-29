package org.darkstorm.darkbot.minecraftbot.protocol.v5x.login.client;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketLC00_LoginStart extends AbstractPacketX implements WriteablePacket {
	private final String name;

	public PacketLC00_LoginStart(String name) {
		super(0x00, State.LOGIN, Direction.UPSTREAM);

		this.name = name;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		writeString(name, out);
	}

	public String getName() {
		return name;
	}
}
