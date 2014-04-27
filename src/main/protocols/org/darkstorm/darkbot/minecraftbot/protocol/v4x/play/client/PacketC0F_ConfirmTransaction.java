package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.client;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketC0F_ConfirmTransaction extends AbstractPacketX implements WriteablePacket {
	private int windowId, actionId;
	private boolean accepted;

	public PacketC0F_ConfirmTransaction(int windowId, int actionId, boolean accepted) {
		super(0x0F, State.PLAY, Direction.UPSTREAM);

		this.windowId = windowId;
		this.actionId = actionId;
		this.accepted = accepted;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeByte(windowId);
		out.writeShort(actionId);
		out.writeBoolean(accepted);
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
