package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class S09PacketChangeHeldItem extends AbstractPacketX implements ReadablePacket {
	private int slot;

	public S09PacketChangeHeldItem() {
		super(0x09, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		slot = in.read();
	}

	public int getSlot() {
		return slot;
	}
}
