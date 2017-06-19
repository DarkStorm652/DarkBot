package org.darkstorm.minecraft.darkbot.protocol.v4x.play.client;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

public class PacketC17_PluginMessage extends AbstractPacketX implements WriteablePacket {
	private String channel;
	private byte[] data;

	public PacketC17_PluginMessage(String channel, byte[] data) {
		super(0x17, State.PLAY, Direction.UPSTREAM);

		this.channel = channel;
		this.data = data;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		writeString(channel, out);
		writeByteArray(data, out);
	}

	public String getChannel() {
		return channel;
	}

	public byte[] getData() {
		return data;
	}
}
