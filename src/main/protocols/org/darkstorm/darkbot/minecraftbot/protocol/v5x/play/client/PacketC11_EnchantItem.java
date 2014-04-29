package org.darkstorm.darkbot.minecraftbot.protocol.v5x.play.client;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketC11_EnchantItem extends AbstractPacketX implements WriteablePacket {

	public PacketC11_EnchantItem() {
		super(0x00, State.PLAY, Direction.UPSTREAM);
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
	}
}
