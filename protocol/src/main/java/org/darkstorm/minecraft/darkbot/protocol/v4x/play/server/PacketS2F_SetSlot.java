package org.darkstorm.minecraft.darkbot.protocol.v4x.play.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;
import org.darkstorm.minecraft.darkbot.world.item.ItemStack;

import java.io.*;

public class PacketS2F_SetSlot extends AbstractPacketX implements ReadablePacket {
	private int windowId, slot;
	private ItemStack item;

	public PacketS2F_SetSlot() {
		super(0x2F, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		windowId = in.readByte();
		slot = in.readShort();
		item = readItemStack(in);
	}

	public int getWindowId() {
		return windowId;
	}

	public int getSlot() {
		return slot;
	}

	public ItemStack getItem() {
		return item;
	}
}
