package org.darkstorm.minecraft.darkbot.protocol.v74.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.world.block.BlockLocation;

import java.io.*;
import java.util.*;

public class Packet60Explosion extends AbstractPacket implements ReadablePacket {
	public double explosionX, explosionY, explosionZ;
	public float explosionSize;
	public Set<BlockLocation> destroyedBlockPositions;

	public float unknownX, unknownY, unknownZ;

	public Packet60Explosion() {
	}

	public void readData(DataInputStream in) throws IOException {
		explosionX = in.readDouble();
		explosionY = in.readDouble();
		explosionZ = in.readDouble();
		explosionSize = in.readFloat();
		int i = in.readInt();
		destroyedBlockPositions = new HashSet<BlockLocation>();
		int j = (int) explosionX;
		int k = (int) explosionY;
		int l = (int) explosionZ;

		for(int i1 = 0; i1 < i; i1++) {
			int j1 = in.readByte() + j;
			int k1 = in.readByte() + k;
			int l1 = in.readByte() + l;
			destroyedBlockPositions.add(new BlockLocation(j1, k1, l1));
		}

		unknownX = in.readFloat();
		unknownY = in.readFloat();
		unknownZ = in.readFloat();
	}

	public int getId() {
		return 60;
	}
}
