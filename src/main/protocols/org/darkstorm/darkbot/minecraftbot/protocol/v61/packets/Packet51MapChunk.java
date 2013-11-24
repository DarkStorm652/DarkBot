package org.darkstorm.darkbot.minecraftbot.protocol.v61.packets;

import java.io.*;
import java.util.zip.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet51MapChunk extends AbstractPacket implements ReadablePacket {
	public int x;
	public int z;
	public int bitmask;
	public int additionalBitmask;
	public boolean biomes;
	public byte[] chunkData;

	public Packet51MapChunk() {
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		x = in.readInt();
		z = in.readInt();
		biomes = in.readBoolean();
		bitmask = in.readShort();
		additionalBitmask = in.readShort();
		int tempLength = in.readInt();

		byte[] compressedChunkData = new byte[tempLength];
		in.readFully(compressedChunkData, 0, tempLength);
		int i = 0;

		for(int j = 0; j < 16; j++)
			i += bitmask >> j & 1;

		int k = 12288 * i;

		if(biomes)
			k += 256;

		chunkData = new byte[k];
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
	}

	@Override
	public int getId() {
		return 51;
	}
}
