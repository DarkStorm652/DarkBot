package org.darkstorm.minecraft.darkbot.protocol.v4x.play.client;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

public class PacketC02_UseEntity extends AbstractPacketX implements WriteablePacket {
	private int entityId, button;

	public PacketC02_UseEntity(int entityId, int button) {
		super(0x02, State.PLAY, Direction.UPSTREAM);

		this.entityId = entityId;
		this.button = button;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeInt(entityId);
		out.write(button);
	}

	public int getEntityId() {
		return entityId;
	}

	public int getButton() {
		return button;
	}
}
