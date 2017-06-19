package org.darkstorm.minecraft.darkbot.protocol.v61.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

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