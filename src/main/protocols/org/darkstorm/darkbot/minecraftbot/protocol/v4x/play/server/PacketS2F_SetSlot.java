package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;
import org.darkstorm.darkbot.minecraftbot.world.item.ItemStack;

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
