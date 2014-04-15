package org.darkstorm.darkbot.minecraftbot.protocol.v4x.handshake;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class PacketHC00_Handshake extends AbstractPacketX implements WriteablePacket {
	private final int protocolVersion;
	private final String address;
	private final int port;
	private final State nextState;

	public PacketHC00_Handshake(int protocolVersion, String address, int port, State nextState) {
		super(0x00, State.HANDSHAKE, Direction.UPSTREAM);

		if(nextState != State.STATUS && nextState != State.LOGIN)
			throw new IllegalArgumentException("Next state must either be status or login");

		this.protocolVersion = protocolVersion;
		this.address = address;
		this.port = port;
		this.nextState = nextState;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		writeVarInt(protocolVersion, out);
		writeString(address, out);
		out.writeShort(port);
		writeVarInt(nextState == State.STATUS ? 1 : 2, out);
	}

	public int getProtocolVersion() {
		return protocolVersion;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public State getNextState() {
		return nextState;
	}
}
