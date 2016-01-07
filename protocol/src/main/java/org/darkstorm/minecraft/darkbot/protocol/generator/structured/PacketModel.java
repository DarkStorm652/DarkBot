package org.darkstorm.minecraft.darkbot.protocol.generator.structured;

import java.io.*;

public interface PacketModel extends CompoundModel {
	public String getName();
	public int getId();
	public StateModel getState();
	public Direction getDirection();
	
	@Override
	public Packet read(InputStream stream) throws IOException;
}
