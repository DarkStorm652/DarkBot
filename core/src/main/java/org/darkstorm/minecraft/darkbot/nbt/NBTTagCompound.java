package org.darkstorm.minecraft.darkbot.nbt;

import java.io.*;
import java.util.*;

public class NBTTagCompound extends NBTBase {
	/**
	 * The key-value pairs for the tag. Each key is a UTF string, each value is
	 * a tag.
	 */
	private Map<String, NBTBase> tagMap;

	public NBTTagCompound() {
		super("");
		tagMap = new HashMap<String, NBTBase>();
	}

	public NBTTagCompound(String par1Str) {
		super(par1Str);
		tagMap = new HashMap<String, NBTBase>();
	}

	/**
	 * Write the actual data contents of the tag, implemented in NBT extension
	 * classes
	 */
	@Override
	void write(DataOutput par1DataOutput) throws IOException {
		NBTBase nbtbase;

		for(Iterator<NBTBase> iterator = tagMap.values().iterator(); iterator
				.hasNext(); NBTBase.writeNamedTag(nbtbase, par1DataOutput)) {
			nbtbase = iterator.next();
		}

		par1DataOutput.writeByte(0);
	}

	/**
	 * Read the actual data contents of the tag, implemented in NBT extension
	 * classes
	 */
	@Override
	void load(DataInput par1DataInput) throws IOException {
		tagMap.clear();
		NBTBase nbtbase;

		for(; (nbtbase = NBTBase.readNamedTag(par1DataInput)).getId() != 0; tagMap
				.put(nbtbase.getName(), nbtbase)) {}
	}

	/**
	 * Returns all the values in the tagMap HashMap.
	 */
	public Collection<NBTBase> getTags() {
		return tagMap.values();
	}

	/**
	 * Gets the type byte for the tag.
	 */
	@Override
	public byte getId() {
		return 10;
	}

	/**
	 * Stores the given tag into the map with the given string key. This is
	 * mostly used to store tag lists.
	 */
	public void setTag(String par1Str, NBTBase par2NBTBase) {
		tagMap.put(par1Str, par2NBTBase.setName(par1Str));
	}

	/**
	 * Stores a new NBTTagByte with the given byte value into the map with the
	 * given string key.
	 */
	public void setByte(String par1Str, byte par2) {
		tagMap.put(par1Str, new NBTTagByte(par1Str, par2));
	}

	/**
	 * Stores a new NBTTagShort with the given short value into the map with the
	 * given string key.
	 */
	public void setShort(String par1Str, short par2) {
		tagMap.put(par1Str, new NBTTagShort(par1Str, par2));
	}

	/**
	 * Stores a new NBTTagInt with the given integer value into the map with the
	 * given string key.
	 */
	public void setInteger(String par1Str, int par2) {
		tagMap.put(par1Str, new NBTTagInt(par1Str, par2));
	}

	/**
	 * Stores a new NBTTagLong with the given long value into the map with the
	 * given string key.
	 */
	public void setLong(String par1Str, long par2) {
		tagMap.put(par1Str, new NBTTagLong(par1Str, par2));
	}

	/**
	 * Stores a new NBTTagFloat with the given float value into the map with the
	 * given string key.
	 */
	public void setFloat(String par1Str, float par2) {
		tagMap.put(par1Str, new NBTTagFloat(par1Str, par2));
	}

	/**
	 * Stores a new NBTTagDouble with the given double value into the map with
	 * the given string key.
	 */
	public void setDouble(String par1Str, double par2) {
		tagMap.put(par1Str, new NBTTagDouble(par1Str, par2));
	}

	/**
	 * Stores a new NBTTagString with the given string value into the map with
	 * the given string key.
	 */
	public void setString(String par1Str, String par2Str) {
		tagMap.put(par1Str, new NBTTagString(par1Str, par2Str));
	}

	/**
	 * Stores a new NBTTagByteArray with the given array as data into the map
	 * with the given string key.
	 */
	public void setByteArray(String par1Str, byte par2ArrayOfByte[]) {
		tagMap.put(par1Str, new NBTTagByteArray(par1Str, par2ArrayOfByte));
	}

	public void func_48183_a(String par1Str, int par2ArrayOfInteger[]) {
		tagMap.put(par1Str, new NBTTagIntArray(par1Str, par2ArrayOfInteger));
	}

	/**
	 * Stores the given NBTTagCompound into the map with the given string key.
	 */
	public void setCompoundTag(String par1Str, NBTTagCompound par2NBTTagCompound) {
		tagMap.put(par1Str, par2NBTTagCompound.setName(par1Str));
	}

	/**
	 * Stores the given boolean value as a NBTTagByte, storing 1 for true and 0
	 * for false, using the given string key.
	 */
	public void setBoolean(String par1Str, boolean par2) {
		setByte(par1Str, ((byte) (par2 ? 1 : 0)));
	}

	/**
	 * gets a generic tag with the specified name
	 */
	public NBTBase getTag(String par1Str) {
		return tagMap.get(par1Str);
	}

	/**
	 * Returns whether the given string has been previously stored as a key in
	 * the map.
	 */
	public boolean hasKey(String par1Str) {
		return tagMap.containsKey(par1Str);
	}

	/**
	 * Retrieves a byte value using the specified key, or 0 if no such key was
	 * stored.
	 */
	public byte getByte(String par1Str) {
		if(!tagMap.containsKey(par1Str)) {
			return 0;
		} else {
			return ((NBTTagByte) tagMap.get(par1Str)).data;
		}
	}

	/**
	 * Retrieves a short value using the specified key, or 0 if no such key was
	 * stored.
	 */
	public short getShort(String par1Str) {
		if(!tagMap.containsKey(par1Str)) {
			return 0;
		} else {
			return ((NBTTagShort) tagMap.get(par1Str)).data;
		}
	}

	/**
	 * Retrieves an integer value using the specified key, or 0 if no such key
	 * was stored.
	 */
	public int getInteger(String par1Str) {
		if(!tagMap.containsKey(par1Str)) {
			return 0;
		} else {
			return ((NBTTagInt) tagMap.get(par1Str)).data;
		}
	}

	/**
	 * Retrieves a long value using the specified key, or 0 if no such key was
	 * stored.
	 */
	public long getLong(String par1Str) {
		if(!tagMap.containsKey(par1Str)) {
			return 0L;
		} else {
			return ((NBTTagLong) tagMap.get(par1Str)).data;
		}
	}

	/**
	 * Retrieves a float value using the specified key, or 0 if no such key was
	 * stored.
	 */
	public float getFloat(String par1Str) {
		if(!tagMap.containsKey(par1Str)) {
			return 0.0F;
		} else {
			return ((NBTTagFloat) tagMap.get(par1Str)).data;
		}
	}

	/**
	 * Retrieves a double value using the specified key, or 0 if no such key was
	 * stored.
	 */
	public double getDouble(String par1Str) {
		if(!tagMap.containsKey(par1Str)) {
			return 0.0D;
		} else {
			return ((NBTTagDouble) tagMap.get(par1Str)).data;
		}
	}

	/**
	 * Retrieves a string value using the specified key, or an empty string if
	 * no such key was stored.
	 */
	public String getString(String par1Str) {
		if(!tagMap.containsKey(par1Str)) {
			return "";
		} else {
			return ((NBTTagString) tagMap.get(par1Str)).data;
		}
	}

	/**
	 * Retrieves a byte array using the specified key, or a zero-length array if
	 * no such key was stored.
	 */
	public byte[] getByteArray(String par1Str) {
		if(!tagMap.containsKey(par1Str)) {
			return new byte[0];
		} else {
			return ((NBTTagByteArray) tagMap.get(par1Str)).byteArray;
		}
	}

	public int[] func_48182_l(String par1Str) {
		if(!tagMap.containsKey(par1Str)) {
			return new int[0];
		} else {
			return ((NBTTagIntArray) tagMap.get(par1Str)).field_48181_a;
		}
	}

	/**
	 * Retrieves a NBTTagCompound subtag matching the specified key, or a new
	 * empty NBTTagCompound if no such key was stored.
	 */
	public NBTTagCompound getCompoundTag(String par1Str) {
		if(!tagMap.containsKey(par1Str)) {
			return new NBTTagCompound(par1Str);
		} else {
			return (NBTTagCompound) tagMap.get(par1Str);
		}
	}

	/**
	 * Retrieves a NBTTagList subtag matching the specified key, or a new empty
	 * NBTTagList if no such key was stored.
	 */
	public NBTTagList getTagList(String par1Str) {
		if(!tagMap.containsKey(par1Str)) {
			return new NBTTagList(par1Str);
		} else {
			return (NBTTagList) tagMap.get(par1Str);
		}
	}

	/**
	 * Retrieves a boolean value using the specified key, or false if no such
	 * key was stored. This uses the getByte method.
	 */
	public boolean getBoolean(String par1Str) {
		return getByte(par1Str) != 0;
	}

	@Override
	public String toString() {
		return (new StringBuilder()).append("").append(tagMap.size())
				.append(" entries").toString();
	}

	/**
	 * Creates a clone of the tag.
	 */
	@Override
	public NBTBase copy() {
		NBTTagCompound nbttagcompound = new NBTTagCompound(getName());
		String s;

		for(Iterator<String> iterator = tagMap.keySet().iterator(); iterator
				.hasNext(); nbttagcompound.setTag(s, tagMap.get(s).copy())) {
			s = iterator.next();
		}

		return nbttagcompound;
	}

	@Override
	public boolean equals(Object par1Obj) {
		if(super.equals(par1Obj)) {
			NBTTagCompound nbttagcompound = (NBTTagCompound) par1Obj;
			return tagMap.entrySet().equals(nbttagcompound.tagMap.entrySet());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ tagMap.hashCode();
	}
}
