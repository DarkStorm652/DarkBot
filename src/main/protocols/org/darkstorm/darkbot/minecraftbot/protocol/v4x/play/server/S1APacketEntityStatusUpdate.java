package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

public class S1APacketEntityStatusUpdate extends S14PacketEntityUpdate {
	private int status;

	public S1APacketEntityStatusUpdate() {
		super(0x1A);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		super.readData(in);

		status = in.read();
	}

	public int getStatus() {
		return status;
	}
}
