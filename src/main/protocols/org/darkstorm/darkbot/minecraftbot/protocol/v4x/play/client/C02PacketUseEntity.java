package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.client;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class C02PacketUseEntity extends AbstractPacketX implements WriteablePacket {
	private int entityId, button;

	public C02PacketUseEntity(int entityId, int button) {
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
