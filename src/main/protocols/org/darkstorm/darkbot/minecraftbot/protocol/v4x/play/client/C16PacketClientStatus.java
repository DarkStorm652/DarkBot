package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.client;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class C16PacketClientStatus extends AbstractPacketX implements WriteablePacket {
	private int action;

	public C16PacketClientStatus(int action) {
		super(0x16, State.PLAY, Direction.UPSTREAM);

		this.action = action;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.write(action);
	}
}
