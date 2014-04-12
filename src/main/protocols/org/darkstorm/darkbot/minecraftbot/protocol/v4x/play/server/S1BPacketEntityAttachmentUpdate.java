package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

public class S1BPacketEntityAttachmentUpdate extends S14PacketEntityUpdate {
	private int attachedEntityId;
	private boolean withLeash;

	public S1BPacketEntityAttachmentUpdate() {
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
