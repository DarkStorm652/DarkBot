package org.darkstorm.minecraft.darkbot.protocol.v61.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet254ServerPing extends AbstractPacket implements
		WriteablePacket {
	public Packet254ServerPing() {
	}

	public void writeData(DataOutputStream dataoutputstream) throws IOException {
	}

	public int getId() {
		return 254;
	}
}
