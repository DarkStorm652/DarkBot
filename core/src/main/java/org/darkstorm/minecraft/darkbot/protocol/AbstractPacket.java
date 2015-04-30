package org.darkstorm.darkbot.minecraftbot.protocol;

import java.io.*;
import java.util.List;

import org.darkstorm.darkbot.minecraftbot.nbt.*;
import org.darkstorm.darkbot.minecraftbot.util.IntHashMap;
import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;
import org.darkstorm.darkbot.minecraftbot.world.entity.WatchableObject;
import org.darkstorm.darkbot.minecraftbot.world.item.*;

public abstract class AbstractPacket implements Packet {
	public static String readString(DataInputStream in) throws IOException {
		return readString(in, 32767);
	}

	public static String readString(DataInputStream in, int maxSize) throws IOException {
		int length = in.readShort();
		if(length > maxSize)
			throw new IOException("String too big");
		char[] characters = new char[length];
		for(int i = 0; i < length; i++)
			characters[i] = in.readChar();
		return new String(characters);
	}

	public static void writeString(String string, DataOutputStream out) throws IOException {
		if(string.length() > 32767)
			throw new IOException("String too big");
		out.writeShort(string.length());
		out.writeChars(string);
	}

	public static ItemStack readItemStack(DataInputStream in) throws IOException {
		ItemStack item = null;
		short id = in.readShort();

		if(id >= 0) {
			byte stackSize = in.readByte();
			short damage = in.readShort();
			item = new BasicItemStack(id, stackSize, damage);
			item.setStackTagCompound(readNBTTagCompound(in));
		}

		return item;
	}

	public static void writeItemStack(ItemStack item, DataOutputStream out) throws IOException {
		if(item != null) {
			out.writeShort(item.getId());
			out.writeByte(item.getStackSize());
			out.writeShort(item.getDamage());

			writeNBTTagCompound(item.getStackTagCompound(), out);
		} else
			out.writeShort(-1);
	}

	public static NBTTagCompound readNBTTagCompound(DataInputStream in) throws IOException {
		short length = in.readShort();

		if(length >= 0) {
			byte[] data = new byte[length];
			in.readFully(data);
			return CompressedStreamTools.decompress(data);
		} else
			return null;
	}

	public static void writeNBTTagCompound(NBTTagCompound compound, DataOutputStream out) throws IOException {
		if(compound != null) {
			byte[] data = CompressedStreamTools.compress(compound);
			out.writeShort((short) data.length);
			out.write(data);
		} else
			out.writeShort(-1);
	}

	public static byte[] readByteArray(DataInputStream in) throws IOException {
		short length = in.readShort();
		if(length >= 0) {
			byte[] read = new byte[length];
			in.read(read);
			return read;
		} else
			throw new IOException();
	}

	public static void writeByteArray(byte[] bytes, DataOutputStream out) throws IOException {
		out.writeShort(bytes.length);
		out.write(bytes);
	}

	public static void writeWatchableObjects(List<WatchableObject> objects, DataOutputStream out) throws IOException {
		for(WatchableObject object : objects)
			writeWatchableObject(out, object);
		out.writeByte(127);
	}

	public static void writeWatchableObjects(IntHashMap<WatchableObject> objects, DataOutputStream out) throws IOException {
		for(WatchableObject object : objects.values())
			writeWatchableObject(out, object);
		out.writeByte(127);
	}

	private static void writeWatchableObject(DataOutputStream out, WatchableObject watchableObject) throws IOException {
		int i = (watchableObject.getObjectType() << 5 | watchableObject.getDataValueId() & 0x1f) & 0xff;
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
			writeString((String) watchableObject.getObject(), out);
			break;
		case 5:
			ItemStack itemstack = (ItemStack) watchableObject.getObject();
			writeItemStack(itemstack, out);
			break;
		case 6:
			BlockLocation chunkcoordinates = (BlockLocation) watchableObject.getObject();
			out.writeInt(chunkcoordinates.getX());
			out.writeInt(chunkcoordinates.getY());
			out.writeInt(chunkcoordinates.getZ());
			break;
		}
	}

	public static IntHashMap<WatchableObject> readWatchableObjects(DataInputStream in) throws IOException {
		IntHashMap<WatchableObject> map = new IntHashMap<WatchableObject>();
		for(byte b = in.readByte(); b != 127; b = in.readByte()) {
			int i = (b & 0xe0) >> 5;
			int j = b & 0x1f;
			WatchableObject watchableobject = null;
			switch(i) {
			case 0:
				watchableobject = new WatchableObject(i, j, Byte.valueOf(in.readByte()));
				break;
			case 1:
				watchableobject = new WatchableObject(i, j, Short.valueOf(in.readShort()));
				break;
			case 2:
				watchableobject = new WatchableObject(i, j, Integer.valueOf(in.readInt()));
				break;
			case 3:
				watchableobject = new WatchableObject(i, j, Float.valueOf(in.readFloat()));
				break;
			case 4:
				watchableobject = new WatchableObject(i, j, readString(in, 64));
				break;
			case 5:
				watchableobject = new WatchableObject(i, j, readItemStack(in));
				break;
			case 6:
				int k = in.readInt();
				int l = in.readInt();
				int i1 = in.readInt();
				watchableobject = new WatchableObject(i, j, new BlockLocation(k, l, i1));
				break;
			}
			map.put(i, watchableobject);
		}
		return map;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}