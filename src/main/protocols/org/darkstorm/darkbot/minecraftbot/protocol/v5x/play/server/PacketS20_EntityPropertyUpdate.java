package org.darkstorm.darkbot.minecraftbot.protocol.v5x.play.server;

import java.io.*;
import java.util.*;

public class PacketS20_EntityPropertyUpdate extends PacketS14_EntityUpdate {
	private EntityProperty[] properties;

	public PacketS20_EntityPropertyUpdate() {
		super(0x20);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		super.readData(in);

		int length = in.readInt();
		EntityProperty[] properties = new EntityProperty[length];
		for(int i = 0; i < length; i++) {
			String name = readString(in);
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
		this.properties = properties;
	}

	public EntityProperty[] getProperties() {
		return properties.clone();
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
			return modifiers.toArray(new Modifier[modifiers.size()]);
		}

		private void addModifier(UUID uuid, double amount, int operation) {
			modifiers.add(new Modifier(uuid, amount, operation));
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
