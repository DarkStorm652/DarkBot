package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.client;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.ProtocolX.State;

public class C03PacketPlayerUpdate extends AbstractPacketX implements WriteablePacket {
	private boolean grounded;

	public C03PacketPlayerUpdate(boolean grounded) {
		super(0x03, State.PLAY, Direction.UPSTREAM);

		this.grounded = grounded;
	}

	protected C03PacketPlayerUpdate(int id, boolean grounded) {
		super(id, State.PLAY, Direction.UPSTREAM);

		this.grounded = grounded;
	}

	@Override
	public void writeData(DataOutputStream out) throws IOException {
		out.writeBoolean(grounded);
	}

	public boolean isGrounded() {
		return grounded;
	}
}
