package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.client;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketC0B_EntityAction extends AbstractPacketX implements WriteablePacket {
	private int entityId, jumpBoost;
	private Action action;

	public PacketC0B_EntityAction(int entityId, Action action, int jumpBoost) {
		super(0x0B, State.PLAY, Direction.UPSTREAM);

		this.entityId = entityId;
		this.action = action;
		this.jumpBoost = jumpBoost;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeInt(entityId);
		out.write(action.getId());
		out.writeInt(jumpBoost);
	}

	public int getEntityId() {
		return entityId;
	}

	public Action getAction() {
		return action;
	}

	public int getJumpBoost() {
		return jumpBoost;
	}

	public enum Action {
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
}
