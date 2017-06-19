package org.darkstorm.minecraft.darkbot.protocol.v5x.play.server;

import java.io.*;

public class PacketS1E_EntityRemoveEffect extends PacketS14_EntityUpdate {
	private int effectId;

	public PacketS1E_EntityRemoveEffect() {
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
