package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class S02PacketChatMessage extends AbstractPacketX implements ReadablePacket {
	private String message;

	public S02PacketChatMessage() {
		super(0x02, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		message = readString(in);
	}

	public String getMessage() {
		return message;
	}
}
