package org.darkstorm.minecraft.darkbot.protocol.v4x.play.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

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
