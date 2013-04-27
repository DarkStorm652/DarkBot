package org.darkstorm.darkbot.minecraftbot.protocol;

import java.io.*;

public interface ReadablePacket extends Packet {
	public void readData(DataInputStream in) throws IOException;
}
