package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

public class S1DPacketEntityEffectUpdate extends S14PacketEntityUpdate {
	private int effectId, amplifier, duration;

	public S1DPacketEntityEffectUpdate() {
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
