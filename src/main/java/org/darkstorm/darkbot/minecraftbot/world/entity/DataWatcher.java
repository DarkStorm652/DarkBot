package org.darkstorm.darkbot.minecraftbot.world.entity;

import java.util.*;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.protocol.AbstractPacket;
import org.darkstorm.darkbot.minecraftbot.util.IntHashMap;
import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;
import org.darkstorm.darkbot.minecraftbot.world.item.ItemStack;

public class DataWatcher {
	private static final HashMap<Class<?>, Integer> dataTypes;
	private final Map<Integer, WatchableObject> watchedObjects = new HashMap<Integer, WatchableObject>();

	// /** true if one or more object was changed */
	// private boolean objectChanged;

	public DataWatcher() {
	}

	/**
	 * adds a new object to dataWatcher to watch, to update an already existing
	 * object see updateObject. Arguments: data Value Id, Object to add
	 */
	public void addObject(int par1, Object par2Obj) {
		Integer integer = dataTypes.get(par2Obj.getClass());

		if(integer == null) {
			throw new IllegalArgumentException((new StringBuilder())
					.append("Unknown data type: ").append(par2Obj.getClass())
					.toString());
		}

		if(par1 > 31) {
			throw new IllegalArgumentException((new StringBuilder())
					.append("Data value id is too big with ").append(par1)
					.append("! (Max is ").append(31).append(")").toString());
		}

		if(watchedObjects.containsKey(Integer.valueOf(par1))) {
			throw new IllegalArgumentException((new StringBuilder())
					.append("Duplicate id value for ").append(par1).append("!")
					.toString());
		} else {
			WatchableObject watchableobject = new WatchableObject(
					integer.intValue(), par1, par2Obj);
			watchedObjects.put(Integer.valueOf(par1), watchableobject);
			return;
		}
	}

	/**
	 * gets the bytevalue of a watchable object
	 */
	public byte getWatchableObjectByte(int par1) {
		return ((Byte) watchedObjects.get(Integer.valueOf(par1)).getObject())
				.byteValue();
	}

	public short getWatchableObjectShort(int par1) {
		return ((Short) watchedObjects.get(Integer.valueOf(par1)).getObject())
				.shortValue();
	}

	/**
	 * gets a watchable object and returns it as a Integer
	 */
	public int getWatchableObjectInt(int par1) {
		return ((Integer) watchedObjects.get(Integer.valueOf(par1)).getObject())
				.intValue();
	}

	/**
	 * gets a watchable object and returns it as a String
	 */
	public String getWatchableObjectString(int par1) {
		return (String) watchedObjects.get(Integer.valueOf(par1)).getObject();
	}

	/**
	 * updates an already existing object
	 */
	public void updateObject(int par1, Object par2Obj) {
		WatchableObject watchableobject = watchedObjects.get(Integer
				.valueOf(par1));

		if(!par2Obj.equals(watchableobject.getObject())) {
			watchableobject.setObject(par2Obj);
			watchableobject.setWatching(true);
			// objectChanged = true;
		}
	}

	/**
	 * writes every object in passed list to dataoutputstream, terminated by
	 * 0x7F
	 */
	public static void writeObjectsInListToStream(
			List<WatchableObject> par0List,
			DataOutputStream par1DataOutputStream) throws IOException {
		if(par0List != null) {
			WatchableObject watchableobject;

			for(Iterator<WatchableObject> iterator = par0List.iterator(); iterator
					.hasNext(); writeWatchableObject(par1DataOutputStream,
					watchableobject)) {
				watchableobject = iterator.next();
			}
		}

		par1DataOutputStream.writeByte(127);
	}

	public static void writeObjectsInListToStream(
			IntHashMap<WatchableObject> par0List,
			DataOutputStream par1DataOutputStream) throws IOException {
		if(par0List != null) {
			WatchableObject watchableobject;

			for(Iterator<WatchableObject> iterator = par0List.values()
					.iterator(); iterator.hasNext(); writeWatchableObject(
					par1DataOutputStream, watchableobject)) {
				watchableobject = iterator.next();
			}
		}

		par1DataOutputStream.writeByte(127);
	}

	public void writeWatchableObjects(DataOutputStream par1DataOutputStream)
			throws IOException {
		WatchableObject watchableobject;

		for(Iterator<WatchableObject> iterator = watchedObjects.values()
				.iterator(); iterator.hasNext(); writeWatchableObject(
				par1DataOutputStream, watchableobject)) {
			watchableobject = iterator.next();
		}

		par1DataOutputStream.writeByte(127);
	}

	private static void writeWatchableObject(DataOutputStream out,
			WatchableObject watchableObject) throws IOException {
		int i = (watchableObject.getObjectType() << 5 | watchableObject
				.getDataValueId() & 0x1f) & 0xff;
		out.writeByte(i);

		switch(watchableObject.getObjectType()) {
		case 0:
			out.writeByte(((Byte) watchableObject.getObject()).byteValue());
			break;

		case 1:
			out.writeShort(((Short) watchableObject.getObject()).shortValue());
			break;

		case 2:
			out.writeInt(((Integer) watchableObject.getObject()).intValue());
			break;

		case 3:
			out.writeFloat(((Float) watchableObject.getObject()).floatValue());
			break;

		case 4:
			AbstractPacket.writeString((String) watchableObject.getObject(),
					out);
			break;

		case 5:
			ItemStack itemstack = (ItemStack) watchableObject.getObject();
			AbstractPacket.writeItemStack(itemstack, out);
			break;

		case 6:
			BlockLocation chunkcoordinates = (BlockLocation) watchableObject
					.getObject();
			out.writeInt(chunkcoordinates.getX());
			out.writeInt(chunkcoordinates.getY());
			out.writeInt(chunkcoordinates.getZ());
			break;
		}
	}

	public static IntHashMap<WatchableObject> readWatchableObjects(
			DataInputStream in) throws IOException {
		IntHashMap<WatchableObject> map = null;

		for(byte byte0 = in.readByte(); byte0 != 127; byte0 = in.readByte()) {
			if(map == null) {
				map = new IntHashMap<WatchableObject>();
			}

			int i = (byte0 & 0xe0) >> 5;
			int j = byte0 & 0x1f;
			WatchableObject watchableobject = null;

			switch(i) {
			case 0:
				watchableobject = new WatchableObject(i, j, Byte.valueOf(in
						.readByte()));
				break;

			case 1:
				watchableobject = new WatchableObject(i, j, Short.valueOf(in
						.readShort()));
				break;

			case 2:
				watchableobject = new WatchableObject(i, j, Integer.valueOf(in
						.readInt()));
				break;

			case 3:
				watchableobject = new WatchableObject(i, j, Float.valueOf(in
						.readFloat()));
				break;

			case 4:
				watchableobject = new WatchableObject(i, j,
						AbstractPacket.readString(in, 64));
				break;

			case 5:
				watchableobject = new WatchableObject(i, j,
						AbstractPacket.readItemStack(in));
				break;

			case 6:
				int k = in.readInt();
				int l = in.readInt();
				int i1 = in.readInt();
				watchableobject = new WatchableObject(i, j, new BlockLocation(
						k, l, i1));
				break;
			}

			map.put(i, watchableobject);
		}

		return map;
	}

	public void updateWatchedObjectsFromList(List<WatchableObject> par1List) {
		Iterator<WatchableObject> iterator = par1List.iterator();

		do {
			if(!iterator.hasNext()) {
				break;
			}

			WatchableObject watchableobject = iterator.next();
			WatchableObject watchableobject1 = watchedObjects.get(Integer
					.valueOf(watchableobject.getDataValueId()));

			if(watchableobject1 != null) {
				watchableobject1.setObject(watchableobject.getObject());
			}
		} while(true);
	}

	static {
		dataTypes = new HashMap<Class<?>, Integer>();
		dataTypes.put(java.lang.Byte.class, Integer.valueOf(0));
		dataTypes.put(java.lang.Short.class, Integer.valueOf(1));
		dataTypes.put(java.lang.Integer.class, Integer.valueOf(2));
		dataTypes.put(java.lang.Float.class, Integer.valueOf(3));
		dataTypes.put(java.lang.String.class, Integer.valueOf(4));
		dataTypes.put(ItemStack.class, Integer.valueOf(5));
		dataTypes.put(BlockLocation.class, Integer.valueOf(6));
	}
}
