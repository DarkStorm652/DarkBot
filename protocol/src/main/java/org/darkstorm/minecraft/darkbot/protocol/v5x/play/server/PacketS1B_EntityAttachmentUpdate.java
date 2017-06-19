package org.darkstorm.minecraft.darkbot.protocol.v5x.play.server;

import java.io.*;

public class PacketS1B_EntityAttachmentUpdate extends PacketS14_EntityUpdate {
	private int attachedEntityId;
	private boolean withLeash;

	public PacketS1B_EntityAttachmentUpdate() {
		super(0x1B);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		super.readData(in);

		attachedEntityId = in.readInt();
		withLeash = in.readBoolean();
	}

	public int getAttachedEntityId() {
		return attachedEntityId;
	}

	public boolean isWithLeash() {
		return withLeash;
	}
}
