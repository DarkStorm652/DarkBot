package org.darkstorm.darkbot.minecraftbot.protocol.readable;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.util.IntHashMap;
import org.darkstorm.darkbot.minecraftbot.world.entity.*;

public class Packet40EntityMetadata extends AbstractPacket implements
		ReadablePacket {
	public int entityId;
	private IntHashMap<WatchableObject> metadata;

	public Packet40EntityMetadata() {
	}

	public void readData(DataInputStream in) throws IOException {
		entityId = in.readInt();
		metadata = DataWatcher.readWatchableObjects(in);
	}

	public int getId() {
		return 40;
	}

	public IntHashMap<WatchableObject> getMetadata() {
		return metadata;
	}
}
