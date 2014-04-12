package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.client;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class C17PacketPluginMessage extends AbstractPacketX implements WriteablePacket {
	private String channel;
	private byte[] data;

	public C17PacketPluginMessage(String channel, byte[] data) {
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
