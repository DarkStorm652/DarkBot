package org.darkstorm.minecraft.darkbot.protocol.v5x.play.client;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

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
