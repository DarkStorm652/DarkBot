package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class S1FPacketExperienceUpdate extends AbstractPacketX implements ReadablePacket {
	private double experience;
	private int level, total;

	public S1FPacketExperienceUpdate() {
		super(0x1F, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		experience = in.readFloat();
		level = in.readShort();
		total = in.readShort();
	}

	public double getExperience() {
		return experience;
	}

	public int getLevel() {
		return level;
	}

	public int getTotal() {
		return total;
	}
}
