package org.darkstorm.darkbot.minecraftbot.protocol;

import java.io.*;

public interface WriteablePacket extends Packet {
	public void writeData(DataOutputStream out) throws IOException;
}
