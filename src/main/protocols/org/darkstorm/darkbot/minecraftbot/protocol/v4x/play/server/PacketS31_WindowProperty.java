package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

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
