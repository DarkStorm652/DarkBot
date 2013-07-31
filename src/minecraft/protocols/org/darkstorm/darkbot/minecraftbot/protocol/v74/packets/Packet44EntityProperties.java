package org.darkstorm.darkbot.minecraftbot.protocol.v74.packets;

import java.io.*;
import java.util.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;

public class Packet44EntityProperties extends AbstractPacket implements ReadablePacket {
	public int entityId;
	public EntityProperty[] properties;

	public Packet44EntityProperties() {
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		entityId = in.readInt();
		int length = in.readInt();
		properties = new EntityProperty[length];
		for(int i = 0; i < length; i++) {
			String name = readString(in, 64);
			double value = in.readDouble();
			EntityProperty property = new EntityProperty(name, value);
			short modifiers = in.readShort();
			for(int j = 0; j < modifiers; j++) {
				long msb = in.readLong();
				long lsb = in.readLong();
				UUID uuid = new UUID(msb, lsb);
				double amount = in.readDouble();
				int operation = in.read();
				property.addModifier(uuid, amount, operation);
			}
			properties[i] = property;
		}
	}

	@Override
	public int getId() {
		return 44;
	}

	public static final class EntityProperty {
		private final String name;
		private final double value;
		private final List<Modifier> modifiers;

		private EntityProperty(String name, double value) {
			this.name = name;
			this.value = value;
			modifiers = new ArrayList<>();
		}

		public String getName() {
			return name;
		}

		public double getValue() {
			return value;
		}

		public Modifier[] getModifiers() {
			synchronized(modifiers) {
				return modifiers.toArray(new Modifier[modifiers.size()]);
			}
		}

		private void addModifier(UUID uuid, double amount, int operation) {
			synchronized(modifiers) {
				modifiers.add(new Modifier(uuid, amount, operation));
			}
		}

		public final class Modifier {
			private final UUID uuid;
			private final double amount;
			private final int operation;

			private Modifier(UUID uuid, double amount, int operation) {
				this.uuid = uuid;
				this.amount = amount;
				this.operation = operation;
			}

			public UUID getUUID() {
				return uuid;
			}

			public double getAmount() {
				return amount;
			}

			public int getOperation() {
				return operation;
			}
		}
	}
}