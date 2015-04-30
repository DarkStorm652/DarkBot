package org.darkstorm.minecraft.darkbot.protocol;

import java.io.*;

public interface Protocol<H extends PacketHeader> {
	// Pre-1.7
	public static final int V1_5_2 = 61;
	public static final int V1_6_1 = 72;
	public static final int V1_6_2 = 74;
	public static final int V1_6_4 = 78;

	// Post-1.7 - X protocols
	public static final int V1_7_2 = 4;

	public int getVersion();

	public H readHeader(DataInputStream in) throws IOException;

	public H createHeader(Packet packet, byte[] data);

	public Packet createPacket(H header);

	public int[] getPacketIds();
}
