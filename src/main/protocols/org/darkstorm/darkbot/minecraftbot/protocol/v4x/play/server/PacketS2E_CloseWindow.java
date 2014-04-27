package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketS2E_CloseWindow extends AbstractPacketX implements ReadablePacket {
	private int windowId;

	public PacketS2E_CloseWindow() {
		super(0x2E, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		windowId = in.readByte();
	}

	public int getWindowId() {
		return windowId;
	}
}
