package org.darkstorm.darkbot.minecraftbot.protocol.v5x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketS39_PlayerAbilities extends AbstractPacketX implements ReadablePacket {
	private static final int CREATIVE_MODE = 0x1, FLYING = 0x2, CAN_FLY = 0x4, GOD_MODE = 0x8;

	private boolean creative, flying, ableToFly, invincible;
	private double walkingSpeed, flyingSpeed;

	public PacketS39_PlayerAbilities() {
		super(0x39, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		int flags = in.read();
		creative = (flags & CREATIVE_MODE) != 0;
		flying = (flags & FLYING) != 0;
		ableToFly = (flags & CAN_FLY) != 0;
		invincible = (flags & GOD_MODE) != 0;

		flyingSpeed = in.readInt() / 250D;
		walkingSpeed = in.readInt() / 250D;
	}

	public boolean isCreative() {
		return creative;
	}

	public boolean isFlying() {
		return flying;
	}

	public boolean isAbleToFly() {
		return ableToFly;
	}

	public boolean isInvincible() {
		return invincible;
	}

	public double getWalkingSpeed() {
		return walkingSpeed;
	}

	public double getFlyingSpeed() {
		return flyingSpeed;
	}
}
