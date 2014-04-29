package org.darkstorm.darkbot.minecraftbot.protocol.v5x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketS3A_TabCompletion extends AbstractPacketX implements ReadablePacket {
	private String[] completions;

	public PacketS3A_TabCompletion() {
		super(0x3A, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		int count = readVarInt(in);
		completions = new String[count];
		for(int i = 0; i < count; i++)
			completions[i] = readString(in);
	}

	public String[] getCompletions() {
		return completions;
	}
}
