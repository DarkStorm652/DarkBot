package org.darkstorm.minecraft.darkbot.protocol;

import java.io.*;

public interface WriteablePacket extends Packet {
	public void writeData(DataOutputStream out) throws IOException;
}
