package org.darkstorm.minecraft.darkbot.protocol.v5x.play.client;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

public class PacketC01_Chat extends AbstractPacketX implements WriteablePacket {
	private String message;

	public PacketC01_Chat(String message) {
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
