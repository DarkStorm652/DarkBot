package org.darkstorm.minecraft.darkbot.protocol.v4x.play.client;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

public class PacketC07_BlockDig extends AbstractPacketX implements WriteablePacket {
	private Action action;
	private int x, y, z, face;

	public PacketC07_BlockDig(Action action, int x, int y, int z, int face) {
		super(0x07, State.PLAY, Direction.UPSTREAM);

		this.action = action;
		this.x = x;
		this.y = y;
		this.z = z;
		this.face = face;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeByte(action.ordinal());
		out.writeInt(x);
		out.writeByte(y);
		out.writeInt(z);
		out.writeByte(face);
	}

	public Action getAction() {
		return action;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public int getFace() {
		return face;
	}

	public enum Action {
		START_DIGGING,
		CANCEL_DIGGING,
		FINISH_DIGGING,
		DROP_ITEM_STACK,
		DROP_ITEM,
		COMPLETE_ITEM_USE
	}
}
