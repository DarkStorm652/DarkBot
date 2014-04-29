package org.darkstorm.darkbot.minecraftbot.protocol.v5x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketS06_UpdateHealth extends AbstractPacketX implements ReadablePacket {
	private double health;
	private int food;
	private double foodSaturation;

	public PacketS06_UpdateHealth() {
		super(0x06, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		health = in.readFloat();
		food = in.readShort();
		foodSaturation = in.readFloat();
	}

	public double getHealth() {
		return health;
	}

	public int getFood() {
		return food;
	}

	public double getFoodSaturation() {
		return foodSaturation;
	}
}
