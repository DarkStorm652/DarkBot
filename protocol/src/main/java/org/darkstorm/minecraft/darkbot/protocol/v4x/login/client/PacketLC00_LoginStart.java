package org.darkstorm.minecraft.darkbot.protocol.v4x.login.client;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

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
