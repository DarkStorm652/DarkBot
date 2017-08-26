package org.darkstorm.minecraft.darkbot.event.protocol.server;

import com.github.steveice10.mc.protocol.data.game.world.block.UpdatedTileType;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import org.darkstorm.minecraft.darkbot.event.protocol.ProtocolEvent;

public class TileEntityUpdateEvent extends ProtocolEvent {
	private final int x;
	private final int y;
	private final int z;
	private final UpdatedTileType type;
	private final CompoundTag compound;

	public TileEntityUpdateEvent(int x, int y, int z, UpdatedTileType type, CompoundTag compound) {
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

	public UpdatedTileType getType() {
		return type;
	}

	public CompoundTag getCompound() {
		return compound;
	}
}
