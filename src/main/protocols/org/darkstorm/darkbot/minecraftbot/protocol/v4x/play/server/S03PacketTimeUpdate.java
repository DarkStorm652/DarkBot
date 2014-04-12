package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class S03PacketTimeUpdate extends AbstractPacketX implements ReadablePacket {
	private long worldAge, time;

	public S03PacketTimeUpdate() {
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
