package org.darkstorm.darkbot.minecraftbot.protocol.v78.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet62NamedSoundEffect extends AbstractPacket implements
		ReadablePacket {
	public String soundName;

	public int xPosition;
	public int yPosition = Integer.MAX_VALUE;
	public int zPosition;

	public float volume;
	public int pitch;

	public Packet62NamedSoundEffect() {
	}

	public void readData(DataInputStream in) throws IOException {
		soundName = readString(in, 32);
		xPosition = in.readInt();
		yPosition = in.readInt();
		zPosition = in.readInt();
		volume = in.readFloat();
		pitch = in.readUnsignedByte();
	}

	@Override
	public int getId() {
		return 62;
	}
}