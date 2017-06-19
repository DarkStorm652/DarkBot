package org.darkstorm.minecraft.darkbot.protocol.v4x.play.server;

import java.io.*;

public class PacketS1D_EntityEffectUpdate extends PacketS14_EntityUpdate {
	private int effectId, amplifier, duration;

	public PacketS1D_EntityEffectUpdate() {
		super(0x1D);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		super.readData(in);

		effectId = in.read();
		amplifier = in.readByte();
		duration = in.readShort();
	}

	public int getEffectId() {
		return effectId;
	}

	public int getAmplifier() {
		return amplifier;
	}

	public int getDuration() {
		return duration;
	}
}
