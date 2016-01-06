package org.darkstorm.minecraft.darkbot.protocol;

import java.io.*;

public interface ReadablePacket extends Packet {
	public void readData(DataInputStream in) throws IOException;
}
