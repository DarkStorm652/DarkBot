package org.darkstorm.minecraft.darkbot.protocol.v5x.play.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

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
