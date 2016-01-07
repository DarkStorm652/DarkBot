package org.darkstorm.minecraft.darkbot.protocol.generator.model;

import org.darkstorm.minecraft.darkbot.protocol.generator.Direction;
import org.darkstorm.minecraft.darkbot.protocol.generator.Packet;

import java.io.*;

public interface PacketModel extends CompoundModel {
	public String getName();
	public int getId();
	public StateModel getState();
	public Direction getDirection();
	
	@Override
	public Packet read(InputStream stream) throws IOException;
}
