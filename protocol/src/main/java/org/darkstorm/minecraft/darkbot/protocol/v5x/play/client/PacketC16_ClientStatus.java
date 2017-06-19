package org.darkstorm.minecraft.darkbot.protocol.v5x.play.client;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

public class PacketC16_ClientStatus extends AbstractPacketX implements WriteablePacket {
	private int action;

	public PacketC16_ClientStatus(int action) {
		super(0x16, State.PLAY, Direction.UPSTREAM);

		this.action = action;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.write(action);
	}
}
