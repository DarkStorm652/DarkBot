package org.darkstorm.minecraft.darkbot.protocol.v5x.play.client;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

public class PacketC09_HeldItemChange extends AbstractPacketX implements WriteablePacket {
	private int slot;

	public PacketC09_HeldItemChange(int slot) {
		super(0x09, State.PLAY, Direction.UPSTREAM);

		this.slot = slot;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeShort(slot);
	}

	public int getSlot() {
		return slot;
	}
}
