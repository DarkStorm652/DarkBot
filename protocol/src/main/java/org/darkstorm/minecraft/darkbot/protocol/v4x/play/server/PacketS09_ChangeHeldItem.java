package org.darkstorm.minecraft.darkbot.protocol.v4x.play.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

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
