package org.darkstorm.darkbot.minecraftbot.protocol.v5x.play.client;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

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
