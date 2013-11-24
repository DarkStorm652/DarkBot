package org.darkstorm.darkbot.minecraftbot.protocol.v74.packets;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet18Animation extends AbstractPacket implements
		ReadablePacket, WriteablePacket {
	public static enum Animation {
		NONE(0),
		SWING_ARM(1),
		DAMAGE_ENTITY(2),
		LEAVE_BED(3),
		EAT_FOOD(5),
		UNKNOWN1(102),
		CROUCH(104),
		UNCROUCH(105);

		private final int id;

		private Animation(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		private static Animation parseAnimation(int id) {
			for(Animation animation : values())
				if(animation.getId() == id)
					return animation;
			return null;
		}
	}

	public int entityId;
	public Animation animation;

	public Packet18Animation() {
	}

	public Packet18Animation(int entityId, Animation animation) {
		this.entityId = entityId;
		this.animation = animation;
	}

	public void readData(DataInputStream in) throws IOException {
		entityId = in.readInt();
		animation = Animation.parseAnimation(in.readByte());
	}

	public void writeData(DataOutputStream out) throws IOException {
		out.writeInt(entityId);
		out.writeByte(animation.getId());
	}

	public int getId() {
		return 18;
	}
}
