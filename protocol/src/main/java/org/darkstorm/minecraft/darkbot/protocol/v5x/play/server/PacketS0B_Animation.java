package org.darkstorm.minecraft.darkbot.protocol.v5x.play.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

public class PacketS0B_Animation extends AbstractPacketX implements ReadablePacket {
	private int playerId;
	private Animation animation;

	public PacketS0B_Animation() {
		super(0x0B, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		playerId = readVarInt(in);
		animation = Animation.getAnimationById(in.read());
	}

	public int getPlayerId() {
		return playerId;
	}

	public Animation getAnimation() {
		return animation;
	}

	public enum Animation {
		NONE(0),
		SWING_ARM(1),
		TAKE_DAMAGE(2),
		LEAVE_BED(3),
		EAT_FOOD(5),
		CRITICAL_EFFECT(6),
		MAGIC_CRITICAL_EFFECT(7),

		UNKNOWN_102(102),
		CROUCH(104),
		UNCROUCH(105);

		private final int id;

		private Animation(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public static Animation getAnimationById(int id) {
			for(Animation animation : values())
				if(id == animation.getId())
					return animation;
			return null;
		}
	}
}
