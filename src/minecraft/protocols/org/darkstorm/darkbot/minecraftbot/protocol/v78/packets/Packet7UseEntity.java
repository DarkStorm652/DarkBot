package org.darkstorm.darkbot.minecraftbot.protocol.v78.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet7UseEntity extends AbstractPacket implements WriteablePacket {
	public int playerEntityId;
	public int targetEntity;
	public int isLeftClick;

	public Packet7UseEntity() {
	}

	public Packet7UseEntity(int playerEntityId, int targetEntity, int isLeftClick) {
		this.playerEntityId = playerEntityId;
		this.targetEntity = targetEntity;
		this.isLeftClick = isLeftClick;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeInt(playerEntityId);
		out.writeInt(targetEntity);
		out.writeByte(isLeftClick);
	}

	@Override
	public int getId() {
		return 7;
	}
}
