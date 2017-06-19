package org.darkstorm.minecraft.darkbot.protocol.v4x.play.client;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

public class PacketC0C_SteerVehicle extends AbstractPacketX implements WriteablePacket {

	public PacketC0C_SteerVehicle() {
		super(0x00, State.PLAY, Direction.UPSTREAM);
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
	}
}
