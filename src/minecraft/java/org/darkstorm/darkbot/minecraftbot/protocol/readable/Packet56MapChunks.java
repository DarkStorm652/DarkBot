package org.darkstorm.darkbot.minecraftbot.protocol.readable;

import java.util.zip.*;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet56MapChunks extends AbstractPacket implements ReadablePacket {
	public int[] chunkX;
	public int[] chunkZ;
	public int[] primaryBitmap;
	public int[] secondaryBitmap;
	public byte[][] chunkData;
	public int chunkDataLength;
	public boolean optionalSpace;
	private static byte[] chunkDataCache = new byte[0];

	public Packet56MapChunks() {
	}

	public void readData(DataInputStream in) throws IOException {
		short chunkLength = in.readShort();
		chunkDataLength = in.readInt();
		optionalSpace = in.readBoolean();
		chunkX = new int[chunkLength];
		chunkZ = new int[chunkLength];
		primaryBitmap = new int[chunkLength];
		secondaryBitmap = new int[chunkLength];
		chunkData = new byte[chunkLength][];

		if(chunkDataCache.length < chunkDataLength) {
			chunkDataCache = new byte[chunkDataLength];
		}

		in.readFully(chunkDataCache, 0, chunkDataLength);
		byte[] chunkData = new byte[196864 * chunkLength];
		Inflater inflater = new Inflater();
		inflater.setInput(chunkDataCache, 0, chunkDataLength);

		try {
			inflater.inflate(chunkData);
		} catch(DataFormatException var11) {
			chunkData = null;
			// throw new IOException("Bad compressed data format");
		} finally {
			inflater.end();
		}

		if(chunkData == null)
			return;

		int index = 0;

		for(int var6 = 0; var6 < chunkLength; ++var6) {
			chunkX[var6] = in.readInt();
			chunkZ[var6] = in.readInt();
			primaryBitmap[var6] = in.readShort();
			secondaryBitmap[var6] = in.readShort();
			int primarySize = 0, secondarySize = 0;

			for(int chunkIndex = 0; chunkIndex < 16; ++chunkIndex) {
				primarySize += primaryBitmap[var6] >> chunkIndex & 1;
				secondarySize += secondaryBitmap[var6] >> chunkIndex & 1;
			}

			int dataLength = 2048 * 4 * primarySize + 256;
			dataLength += 2048 * secondarySize;

			if(optionalSpace)
				dataLength += 2048 * primarySize;

			this.chunkData[var6] = new byte[dataLength];
			System.arraycopy(chunkData, index, this.chunkData[var6], 0,
					dataLength);
			index += dataLength;
		}
	}

	public int getId() {
		return 56;
	}
}