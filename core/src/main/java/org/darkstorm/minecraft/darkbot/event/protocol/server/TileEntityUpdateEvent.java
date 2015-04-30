package org.darkstorm.minecraft.darkbot.event.protocol.server;

import org.darkstorm.minecraft.darkbot.event.protocol.ProtocolEvent;
import org.darkstorm.minecraft.darkbot.nbt.NBTTagCompound;

public class TileEntityUpdateEvent extends ProtocolEvent {
	private final int x;
	private final int y;
	private final int z;
	private final int type;
	private final NBTTagCompound compound;

	public TileEntityUpdateEvent(int x, int y, int z, int type, NBTTagCompound compound) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
		this.compound = compound;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public int getType() {
		return type;
	}

	public NBTTagCompound getCompound() {
		return compound;
	}
}
