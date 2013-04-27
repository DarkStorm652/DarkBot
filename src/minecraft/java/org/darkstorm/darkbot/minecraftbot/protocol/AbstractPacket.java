package org.darkstorm.darkbot.minecraftbot.protocol;

import java.io.*;

import org.darkstorm.darkbot.minecraftbot.nbt.*;
import org.darkstorm.darkbot.minecraftbot.world.item.*;

public abstract class AbstractPacket implements Packet {
	private static final boolean[] damageableItems = new boolean[3200];

	static {
		damageableItems[256] = true;
		damageableItems[257] = true;
		damageableItems[258] = true;
		damageableItems[259] = true;
		damageableItems[261] = true;
		damageableItems[267] = true;
		damageableItems[268] = true;
		damageableItems[269] = true;
		damageableItems[270] = true;
		damageableItems[271] = true;
		damageableItems[272] = true;
		damageableItems[273] = true;
		damageableItems[274] = true;
		damageableItems[275] = true;
		damageableItems[276] = true;
		damageableItems[277] = true;
		damageableItems[278] = true;
		damageableItems[279] = true;
		damageableItems[283] = true;
		damageableItems[284] = true;
		damageableItems[285] = true;
		damageableItems[286] = true;
		damageableItems[290] = true;
		damageableItems[291] = true;
		damageableItems[292] = true;
		damageableItems[293] = true;
		damageableItems[294] = true;
		damageableItems[298] = true;
		damageableItems[299] = true;
		damageableItems[300] = true;
		damageableItems[301] = true;
		damageableItems[302] = true;
		damageableItems[303] = true;
		damageableItems[304] = true;
		damageableItems[305] = true;
		damageableItems[306] = true;
		damageableItems[307] = true;
		damageableItems[308] = true;
		damageableItems[309] = true;
		damageableItems[310] = true;
		damageableItems[311] = true;
		damageableItems[312] = true;
		damageableItems[313] = true;
		damageableItems[314] = true;
		damageableItems[315] = true;
		damageableItems[316] = true;
		damageableItems[317] = true;
		damageableItems[346] = true;
		damageableItems[359] = true;
	}

	public static String readString(DataInputStream in) throws IOException {
		return readString(in, 32767);
	}

	public static String readString(DataInputStream in, int maxSize)
			throws IOException {
		int length = in.readShort();
		if(length > maxSize)
			throw new IOException("String too big");
		char[] characters = new char[length];
		for(int i = 0; i < length; i++)
			characters[i] = in.readChar();
		return new String(characters);
	}

	public static void writeString(String string, DataOutputStream out)
			throws IOException {
		if(string.length() > 32767)
			throw new IOException("String too big");
		out.writeShort(string.length());
		out.writeChars(string);
	}

	public static ItemStack readItemStack(DataInputStream in)
			throws IOException {
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

	/**
	 * Writes the ItemStack's ID (short), then size (byte), then damage. (short)
	 */
	public static void writeItemStack(ItemStack item, DataOutputStream out)
			throws IOException {
		if(item == null) {
			out.writeShort(-1);
		} else {
			out.writeShort(item.getId());
			out.writeByte(item.getStackSize());
			out.writeShort(item.getDamage());

			writeNBTTagCompound(item.getStackTagCompound(), out);
		}
	}

	/**
	 * Reads a compressed NBTTagCompound from the InputStream
	 */
	public static NBTTagCompound readNBTTagCompound(
			DataInputStream par1DataInputStream) throws IOException {
		short word0 = par1DataInputStream.readShort();

		if(word0 < 0) {
			return null;
		} else {
			byte abyte0[] = new byte[word0];
			par1DataInputStream.readFully(abyte0);
			return CompressedStreamTools.decompress(abyte0);
		}
	}

	/**
	 * Writes a compressed NBTTagCompound to the OutputStream
	 */
	public static void writeNBTTagCompound(NBTTagCompound par1NBTTagCompound,
			DataOutputStream par2DataOutputStream) throws IOException {
		if(par1NBTTagCompound == null) {
			par2DataOutputStream.writeShort(-1);
		} else {
			byte abyte0[] = CompressedStreamTools.compress(par1NBTTagCompound);
			par2DataOutputStream.writeShort((short) abyte0.length);
			par2DataOutputStream.write(abyte0);
		}
	}

	public static byte[] readByteArray(DataInputStream in) throws IOException {
		short len = in.readShort();
		if(len >= 0) {
			byte[] read = new byte[len];
			in.read(read);
			return read;
		} else
			throw new IOException();
	}

	public static void writeByteArray(byte[] bytes, DataOutputStream out)
			throws IOException {
		out.writeShort(bytes.length);
		out.write(bytes);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
