package org.darkstorm.minecraft.darkbot.protocol.v61.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.world.item.ItemStack;

import java.io.*;

public class Packet15Place extends AbstractPacket implements WriteablePacket {
	public int xPosition, yPosition, zPosition;
	public float xOffset, yOffset, zOffset;

	public int direction;
	public ItemStack itemStack;

	public Packet15Place() {
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeInt(xPosition);
		out.write(yPosition);
		out.writeInt(zPosition);
		out.write(direction);
		writeItemStack(itemStack, out);
		out.write((int) (xOffset * 16F));
		out.write((int) (yOffset * 16F));
		out.write((int) (zOffset * 16F));
	}

	@Override
	public int getId() {
		return 15;
	}
}
