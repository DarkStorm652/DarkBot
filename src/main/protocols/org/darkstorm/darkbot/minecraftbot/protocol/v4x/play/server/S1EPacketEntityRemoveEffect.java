package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

public class S1EPacketEntityRemoveEffect extends S14PacketEntityUpdate {
	private int effectId;

	public S1EPacketEntityRemoveEffect() {
		super(0x1E);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		super.readData(in);

		effectId = in.read();
	}

	public int getEffectId() {
		return effectId;
	}
}
