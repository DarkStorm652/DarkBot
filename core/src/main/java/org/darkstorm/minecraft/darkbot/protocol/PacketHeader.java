package org.darkstorm.minecraft.darkbot.protocol;

import java.io.*;

public class PacketHeader {
	private final int id;

	public PacketHeader(int id) {
		this.id = id;
	}

	public final int getId() {
		return id;
	}

	public void write(DataOutputStream out) throws IOException {
		out.writeInt(id);
	}

	@Override
	public String toString() {
		return "PacketHeader{id=0x" + Integer.toHexString(getId()).toUpperCase() + "}";
	}
}