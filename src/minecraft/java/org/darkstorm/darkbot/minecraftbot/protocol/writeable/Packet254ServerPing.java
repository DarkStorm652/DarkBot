package org.darkstorm.darkbot.minecraftbot.protocol.writeable;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

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
