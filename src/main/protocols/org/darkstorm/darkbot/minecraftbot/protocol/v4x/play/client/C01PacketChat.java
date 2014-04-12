package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.client;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class C01PacketChat extends AbstractPacketX implements WriteablePacket {
	private String message;

	public C01PacketChat(String message) {
		super(0x01, State.PLAY, Direction.UPSTREAM);

		this.message = message;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		writeString(message, out);
	}

	public String getMessage() {
		return message;
	}
}
