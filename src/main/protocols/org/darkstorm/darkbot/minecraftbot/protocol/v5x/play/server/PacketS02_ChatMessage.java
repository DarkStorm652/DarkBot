package org.darkstorm.darkbot.minecraftbot.protocol.v5x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketS02_ChatMessage extends AbstractPacketX implements ReadablePacket {
	private String message;

	public PacketS02_ChatMessage() {
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
