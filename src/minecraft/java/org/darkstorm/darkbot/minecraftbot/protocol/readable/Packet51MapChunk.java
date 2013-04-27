package org.darkstorm.darkbot.minecraftbot.protocol.readable;

import java.util.zip.*;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet51MapChunk extends AbstractPacket implements ReadablePacket {
	public int x;
	public int z;
	public int bitmask;
	public int additionalBitmask;
	public byte[] chunkData;

	public boolean includeInitialize;
	private static byte[] compressedCache = new byte[0];

	public Packet51MapChunk() {
	}

	public void readData(DataInputStream in) throws IOException {
		x = in.readInt();
		z = in.readInt();
		includeInitialize = in.readBoolean();
		bitmask = in.readShort();
		additionalBitmask = in.readShort();
		int tempLength = in.readInt();

		if(compressedCache.length < tempLength)
			compressedCache = new byte[tempLength];

		in.readFully(compressedCache, 0, tempLength);
		int i = 0;

		for(int j = 0; j < 16; j++)
			i += bitmask >> j & 1;

		int k = 12288 * i;

		if(includeInitialize)
			k += 256;

		chunkData = new byte[k];
		Inflater inflater = new Inflater();
		inflater.setInput(compressedCache, 0, tempLength);

		try {
			inflater.inflate(chunkData);
		} catch(DataFormatException dataformatexception) {
			chunkData = null;
		} catch(OutOfMemoryError error) {
			System.gc();
			try {
				inflater.end();

				inflater = new Inflater();
				inflater.setInput(compressedCache, 0, tempLength);

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

	public int getId() {
		return 51;
	}
}
