package org.darkstorm.minecraft.darkbot.protocol.v5x.play.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

public class PacketS03_TimeUpdate extends AbstractPacketX implements ReadablePacket {
	private long worldAge, time;

	public PacketS03_TimeUpdate() {
		super(0x03, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		worldAge = in.readLong();
		time = in.readLong();
	}

	public long getWorldAge() {
		return worldAge;
	}

	public long getTime() {
		return time;
	}
}
