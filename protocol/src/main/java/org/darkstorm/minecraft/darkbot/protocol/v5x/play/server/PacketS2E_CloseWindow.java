package org.darkstorm.minecraft.darkbot.protocol.v5x.play.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

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
