package org.darkstorm.darkbot.minecraftbot.protocol.v5x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;
import org.darkstorm.darkbot.minecraftbot.world.item.InventoryType;

public class PacketS2D_OpenWindow extends AbstractPacketX implements ReadablePacket {
	private int windowId;
	private InventoryType inventoryType;
	private String windowTitle;
	private int slotCount;
	private boolean useWindowTitle;
	private int entityId;

	public PacketS2D_OpenWindow() {
		super(0x2D, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		windowId = in.readByte() & 255;
		inventoryType = InventoryType.byId(in.readByte() & 255);
		windowTitle = readString(in);
		slotCount = in.readByte() & 255;
		useWindowTitle = in.readBoolean();
		if(inventoryType == InventoryType.ANIMAL_CHEST)
			entityId = in.readInt();
	}

	public int getWindowId() {
		return windowId;
	}

	public InventoryType getInventoryType() {
		return inventoryType;
	}

	public String getWindowTitle() {
		return windowTitle;
	}

	public int getSlotCount() {
		return slotCount;
	}

	public boolean useWindowTitle() {
		return useWindowTitle;
	}

	public int getEntityId() {
		return entityId;
	}
}
