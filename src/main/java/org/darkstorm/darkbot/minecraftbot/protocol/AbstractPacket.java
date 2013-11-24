package org.darkstorm.darkbot.minecraftbot.protocol;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.nbt.*;
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

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}