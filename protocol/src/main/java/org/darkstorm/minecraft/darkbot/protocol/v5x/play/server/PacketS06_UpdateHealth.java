package org.darkstorm.minecraft.darkbot.protocol.v5x.play.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

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
