package org.darkstorm.minecraft.darkbot.protocol.v5x.play.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;
import java.util.zip.*;

public class PacketS21_ChunkData extends AbstractPacketX implements ReadablePacket {
	private ChunkData chunk;
	private boolean biomes;

	public PacketS21_ChunkData() {
		super(0x21, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		int x = in.readInt();
		int z = in.readInt();
		biomes = in.readBoolean();
		int primaryBitmask = in.readShort();
		int secondaryBitmask = in.readShort();

		int tempLength = in.readInt();
		byte[] compressedChunkData = new byte[tempLength];
		in.readFully(compressedChunkData, 0, tempLength);
		int i = 0;

		for(int j = 0; j < 16; j++)
			i += primaryBitmask >> j & 1;

		int k = 12288 * i;

		if(biomes)
			k += 256;

		byte[] chunkData = new byte[k];
		Inflater inflater = new Inflater();
		inflater.setInput(compressedChunkData, 0, tempLength);

		try {
			inflater.inflate(chunkData);
		} catch(DataFormatException dataformatexception) {
			chunkData = null;
		} catch(OutOfMemoryError error) {
			System.gc();
			try {
				inflater.end();

				inflater = new Inflater();
				inflater.setInput(compressedChunkData, 0, tempLength);

				inflater.inflate(chunkData);
			} catch(DataFormatException dataformatexception) {
				chunkData = null;
			} catch(OutOfMemoryError error2) {
				chunkData = null;
			}
		} finally {
			inflater.end();
		}

		if(chunkData != null)
			chunk = new ChunkData(x, z, primaryBitmask, secondaryBitmask, chunkData);
	}

	public ChunkData getChunk() {
		return chunk;
	}

	public boolean hasBiomes() {
		return biomes;
	}

	public static final class ChunkData {
		private final int x, z;

		private final int primaryBitmask, secondaryBitmask;
		private final byte[] data;

		protected ChunkData(int x, int z, int primaryBitmask, int secondaryBitmask, byte[] data) {
			this.x = x;
			this.z = z;
			this.primaryBitmask = primaryBitmask;
			this.secondaryBitmask = secondaryBitmask;
			this.data = data;
		}

		public int getX() {
			return x;
		}

		public int getZ() {
			return z;
		}

		public int getPrimaryBitmask() {
			return primaryBitmask;
		}

		public int getSecondaryBitmask() {
			return secondaryBitmask;
		}

		public byte[] getData() {
			return data;
		}
	}
}
