package org.darkstorm.darkbot.minecraftbot.protocol;

import java.io.*;

public class PacketLengthHeader extends PacketHeader {
	private final int length;

	public PacketLengthHeader(int id, int length) {
		super(id);

		this.length = length;
	}

	public final int getLength() {
		return length;
	}

	@Override
	public void write(DataOutputStream out) throws IOException {
		AbstractPacketX.writeVarInt(getLength(), out);
		AbstractPacketX.writeVarInt(getId(), out);
	}

	@Override
	public String toString() {
		return "PacketLengthHeader{id=0x" + Integer.toHexString(getId()).toUpperCase() + ",length=" + getLength() + "}";
	}
}