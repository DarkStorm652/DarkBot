package org.darkstorm.darkbot.minecraftbot.protocol;

public interface Protocol {
	public static final int V1_5_2 = 61;
	public static final int V1_6_1 = 72;
	public static final int V1_6_2 = 74;

	public int getVersion();

	public Packet createPacket(int id);

	public int[] getPacketIds();
}
