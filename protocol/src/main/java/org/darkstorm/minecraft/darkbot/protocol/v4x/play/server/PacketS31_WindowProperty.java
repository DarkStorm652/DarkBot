package org.darkstorm.minecraft.darkbot.protocol.v4x.play.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

public class PacketS31_WindowProperty extends AbstractPacketX implements ReadablePacket {
	private int windowId, property, value;

	public PacketS31_WindowProperty() {
		super(0x31, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		windowId = in.readByte();
		property = in.readShort();
		value = in.readShort();
	}

	public int getWindowId() {
		return windowId;
	}

	public int getProperty() {
		return property;
	}

	public int getValue() {
		return value;
	}
}
