package org.darkstorm.minecraft.darkbot.protocol.v74.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet19EntityAction extends AbstractPacket implements WriteablePacket {
	public static enum Action {
		CROUCH(1),
		UNCROUCH(2),
		LEAVE_BED(3),
		START_SPRINTING(4),
		STOP_SPRINTING(5);

		private final int id;

		private Action(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}
	}

	public int entityId;
	public Action action;
	public int jumpBoost;

	public Packet19EntityAction() {
	}

	public Packet19EntityAction(int entityId, Action action, int jumpBoost) {
		this.entityId = entityId;
		this.action = action;
		this.jumpBoost = jumpBoost;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeInt(entityId);
		out.writeByte(action.getId());
		out.writeInt(jumpBoost);
	}

	@Override
	public int getId() {
		return 19;
	}
}
