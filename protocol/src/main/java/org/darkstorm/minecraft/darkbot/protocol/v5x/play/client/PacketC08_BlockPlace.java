package org.darkstorm.minecraft.darkbot.protocol.v5x.play.client;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;
import org.darkstorm.minecraft.darkbot.world.item.ItemStack;

import java.io.*;

public class PacketC08_BlockPlace extends AbstractPacketX implements WriteablePacket {
	private int x, y, z, direction;
	private float offsetX, offsetY, offsetZ;
	private ItemStack item;

	public PacketC08_BlockPlace(int x, int y, int z, int direction, ItemStack item, float offsetX, float offsetY, float offsetZ) {
		super(0x08, State.PLAY, Direction.UPSTREAM);

		this.x = x;
		this.y = y;
		this.z = z;
		this.direction = direction;

		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;

		this.item = item;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeInt(x);
		out.writeByte(y);
		out.writeInt(z);
		out.writeByte(direction);
		writeItemStack(item, out);
		out.writeByte((int) (offsetX * 16F));
		out.writeByte((int) (offsetY * 16F));
		out.writeByte((int) (offsetZ * 16F));
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

	@Override
	public Direction getDirection() {
		return super.getDirection();
	}

	public ItemStack getItem() {
		return item;
	}

	public float getOffsetX() {
		return offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}

	public float getOffsetZ() {
		return offsetZ;
	}
}
