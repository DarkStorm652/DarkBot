package org.darkstorm.minecraft.darkbot.protocol.v4x.play.client;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;
import org.darkstorm.minecraft.darkbot.world.item.ItemStack;

import java.io.*;

public class PacketC0E_ClickWindow extends AbstractPacketX implements WriteablePacket {
	private int windowId, slot, button, actionId, mode;
	private ItemStack item;

	public PacketC0E_ClickWindow(int windowId, int slot, int button, int actionId, int mode, ItemStack item) {
		super(0x0E, State.PLAY, Direction.UPSTREAM);

		this.windowId = windowId;
		this.slot = slot;
		this.button = button;
		this.actionId = actionId;
		this.mode = mode;
		this.item = item;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeByte(windowId);
		out.writeShort(slot);
		out.writeByte(button);
		out.writeShort(actionId);
		out.writeByte(mode);
		writeItemStack(item, out);
	}

	public int getWindowId() {
		return windowId;
	}

	public int getSlot() {
		return slot;
	}

	public int getButton() {
		return button;
	}

	public int getActionId() {
		return actionId;
	}

	public int getMode() {
		return mode;
	}

	public ItemStack getItem() {
		return item;
	}
}
