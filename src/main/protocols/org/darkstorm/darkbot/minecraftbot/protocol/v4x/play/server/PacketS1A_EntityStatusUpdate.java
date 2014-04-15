package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

public class PacketS1A_EntityStatusUpdate extends PacketS14_EntityUpdate {
	private int status;

	public PacketS1A_EntityStatusUpdate() {
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
