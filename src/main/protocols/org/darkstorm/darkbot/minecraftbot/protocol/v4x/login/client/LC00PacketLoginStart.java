package org.darkstorm.darkbot.minecraftbot.protocol.v4x.login.client;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class LC00PacketLoginStart extends AbstractPacketX implements WriteablePacket {
	private final String name;

	public LC00PacketLoginStart(String name) {
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
