package org.darkstorm.darkbot.minecraftbot.protocol;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import org.darkstorm.darkbot.minecraftbot.util.IntHashMap;

public abstract class AbstractProtocol implements Protocol {
	private final int version;
	private final IntHashMap<Class<? extends Packet>> packets;

	public AbstractProtocol(int version) {
		this.version = version;
		packets = new IntHashMap<>(256);
	}

	protected final void register(Class<? extends Packet> packetClass) {
		if(packetClass == null)
			throw new NullPointerException();
		Constructor<? extends Packet> constructor;
		try {
			constructor = packetClass.getConstructor();
		} catch(Exception exception) {
			throw new IllegalArgumentException("No default constructor for " + packetClass.getSimpleName());
		}
		Packet packet;
		try {
			packet = constructor.newInstance();
		} catch(Exception exception) {
			throw new IllegalArgumentException(exception);
		}
		int id = packet.getId();
		if(packets.get(id) != null)
			throw new IllegalArgumentException("Duplicate packet ID " + id);
		packets.put(id, packetClass);
	}

	@Override
	public int getVersion() {
		return version;
	}

	@Override
	public Packet createPacket(int id) {
		try {
			return packets.get(id).newInstance();
		} catch(Exception exception) {
			return null;
		}
	}

	@Override
	public int[] getPacketIds() {
		int[] ids = new int[256];
		int length = 0;
		for(int i = 0; i < 256; i++)
			if(packets.get(i) != null)
				ids[length++] = i;
		return Arrays.copyOfRange(ids, 0, length);
	}
}
