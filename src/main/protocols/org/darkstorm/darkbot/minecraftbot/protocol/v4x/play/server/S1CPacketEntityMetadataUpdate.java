package org.darkstorm.darkbot.minecraftbot.protocol.v4x.play.server;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.util.IntHashMap;
import org.darkstorm.darkbot.minecraftbot.world.entity.WatchableObject;

public class S1CPacketEntityMetadataUpdate extends S14PacketEntityUpdate {
	private IntHashMap<WatchableObject> metadata;

	public S1CPacketEntityMetadataUpdate() {
		super(0x1C);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		super.readData(in);

		metadata = readWatchableObjects(in);
	}

	public IntHashMap<WatchableObject> getMetadata() {
		return metadata;
	}
}
