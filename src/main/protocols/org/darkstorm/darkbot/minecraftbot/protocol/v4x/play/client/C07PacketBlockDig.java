package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.client;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class C07PacketBlockDig extends AbstractPacketX implements WriteablePacket {
	private Action action;
	private int x, y, z, face;

	public C07PacketBlockDig(Action action, int x, int y, int z, int face) {
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
