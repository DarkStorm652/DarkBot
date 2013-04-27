package org.darkstorm.darkbot.minecraftbot.protocol.writeable;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.world.item.ItemStack;

public class Packet15Place extends AbstractPacket implements WriteablePacket {
	public int xPosition, yPosition, zPosition;
	public float xOffset, yOffset, zOffset;

	public int direction;
	public ItemStack itemStack;

	public Packet15Place() {
	}

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

	public int getId() {
		return 15;
	}
}
