package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.client;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketC0D_CloseWindow extends AbstractPacketX implements WriteablePacket {
	private int windowId;

	public PacketC0D_CloseWindow(int windowId) {
		super(0x0D, State.PLAY, Direction.UPSTREAM);

		this.windowId = windowId;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeByte(windowId);
	}

	public int getWindowId() {
		return windowId;
	}
}
