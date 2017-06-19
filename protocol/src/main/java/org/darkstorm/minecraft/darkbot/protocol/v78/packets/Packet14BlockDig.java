package org.darkstorm.minecraft.darkbot.protocol.v78.packets;

import org.darkstorm.minecraft.darkbot.protocol.*;

import java.io.*;

public class Packet14BlockDig extends AbstractPacket implements WriteablePacket {
	public int xPosition;
	public int yPosition;
	public int zPosition;

	public int face;
	public int status;

	public Packet14BlockDig() {
	}

	public Packet14BlockDig(int par1, int par2, int par3, int par4, int par5) {
		status = par1;
		xPosition = par2;
		yPosition = par3;
		zPosition = par4;
		face = par5;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.write(status);
		out.writeInt(xPosition);
		out.write(yPosition);
		out.writeInt(zPosition);
		out.write(face);
	}

	@Override
	public int getId() {
		return 14;
	}
}
