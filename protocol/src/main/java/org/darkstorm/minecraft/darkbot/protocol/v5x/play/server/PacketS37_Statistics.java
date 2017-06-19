package org.darkstorm.minecraft.darkbot.protocol.v5x.play.server;

import org.darkstorm.minecraft.darkbot.protocol.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;

import java.io.*;

public class PacketS37_Statistics extends AbstractPacketX implements ReadablePacket {
	private Statistic[] statistics;

	public PacketS37_Statistics() {
		super(0x37, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		int count = readVarInt(in);
		statistics = new Statistic[count];
		for(int i = 0; i < count; i++) {
			String name = readString(in);
			int value = readVarInt(in);
			statistics[i] = new Statistic(name, value);
		}
	}

	public Statistic[] getStatistics() {
		return statistics.clone();
	}

	public static class Statistic {
		private String name;
		private int value;

		private Statistic(String name, int value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public int getValue() {
			return value;
		}
	}
}
