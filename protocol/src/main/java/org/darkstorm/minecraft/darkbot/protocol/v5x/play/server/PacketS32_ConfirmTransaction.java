package org.darkstorm.minecraft.darkbot.protocol.v5x.play.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

public class PacketS32_ConfirmTransaction extends AbstractPacketX implements ReadablePacket {
	private int windowId, actionId;
	private boolean accepted;

	public PacketS32_ConfirmTransaction() {
		super(0x32, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		windowId = in.readByte();
		actionId = in.readShort();
		accepted = in.readBoolean();
	}

	public int getWindowId() {
		return windowId;
	}

	public int getActionId() {
		return actionId;
	}

	public boolean isAccepted() {
		return accepted;
	}
}
