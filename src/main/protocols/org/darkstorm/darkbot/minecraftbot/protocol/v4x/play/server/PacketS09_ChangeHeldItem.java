package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketS09_ChangeHeldItem extends AbstractPacketX implements ReadablePacket {
	private int slot;

	public PacketS09_ChangeHeldItem() {
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
