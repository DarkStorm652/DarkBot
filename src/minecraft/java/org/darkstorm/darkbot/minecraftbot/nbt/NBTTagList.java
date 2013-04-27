package org.darkstorm.darkbot.minecraftbot.nbt;

import java.io.*;
import java.util.*;

public class NBTTagList extends NBTBase {
	/** The array list containing the tags encapsulated in this list. */
	private List<NBTBase> tagList;

	/**
	 * The type byte for the tags in the list - they must all be of the same
	 * type.
	 */
	private byte tagType;

	public NBTTagList() {
		super("");
		tagList = new ArrayList<NBTBase>();
	}

	public NBTTagList(String par1Str) {
		super(par1Str);
		tagList = new ArrayList<NBTBase>();
	}

	/**
	 * Write the actual data contents of the tag, implemented in NBT extension
	 * classes
	 */
	@Override
	void write(DataOutput par1DataOutput) throws IOException {
		if(tagList.size() > 0) {
			tagType = tagList.get(0).getId();
		} else {
			tagType = 1;
		}

		par1DataOutput.writeByte(tagType);
		par1DataOutput.writeInt(tagList.size());

		for(int i = 0; i < tagList.size(); i++) {
			tagList.get(i).write(par1DataOutput);
		}
	}

	/**
	 * Read the actual data contents of the tag, implemented in NBT extension
	 * classes
	 */
	@Override
	void load(DataInput par1DataInput) throws IOException {
		tagType = par1DataInput.readByte();
		int i = par1DataInput.readInt();
		tagList = new ArrayList<NBTBase>();

		for(int j = 0; j < i; j++) {
			NBTBase nbtbase = NBTBase.newTag(tagType, null);
			nbtbase.load(par1DataInput);
			tagList.add(nbtbase);
		}
	}

	/**
	 * Gets the type byte for the tag.
	 */
	@Override
	public byte getId() {
		return 9;
	}

	@Override
	public String toString() {
		return (new StringBuilder()).append("").append(tagList.size())
				.append(" entries of type ")
				.append(NBTBase.getTagName(tagType)).toString();
	}

	/**
	 * Adds the provided tag to the end of the list. There is no check to veriy
	 * this tag is of the same type as any previous tag.
	 */
	public void appendTag(NBTBase par1NBTBase) {
		tagType = par1NBTBase.getId();
		tagList.add(par1NBTBase);
	}

	/**
	 * Retrieves the tag at the specified index from the list.
	 */
	public NBTBase tagAt(int par1) {
		return tagList.get(par1);
	}

	/**
	 * Returns the number of tags in the list.
	 */
	public int tagCount() {
		return tagList.size();
	}

	/**
	 * Creates a clone of the tag.
	 */
	@Override
	public NBTBase copy() {
		NBTTagList nbttaglist = new NBTTagList(getName());
		nbttaglist.tagType = tagType;
		NBTBase nbtbase1;

		for(Iterator<NBTBase> iterator = tagList.iterator(); iterator.hasNext(); nbttaglist.tagList
				.add(nbtbase1)) {
			NBTBase nbtbase = iterator.next();
			nbtbase1 = nbtbase.copy();
		}

		return nbttaglist;
	}

	@Override
	public boolean equals(Object par1Obj) {
		if(super.equals(par1Obj)) {
			NBTTagList nbttaglist = (NBTTagList) par1Obj;

			if(tagType == nbttaglist.tagType) {
				return tagList.equals(nbttaglist.tagList);
			}
		}

		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ tagList.hashCode();
	}
}
