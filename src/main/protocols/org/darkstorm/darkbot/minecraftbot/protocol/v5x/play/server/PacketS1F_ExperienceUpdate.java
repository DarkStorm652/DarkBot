package org.darkstorm.darkbot.minecraftbot.protocol.v5x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketS1F_ExperienceUpdate extends AbstractPacketX implements ReadablePacket {
	private double experience;
	private int level, total;

	public PacketS1F_ExperienceUpdate() {
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
