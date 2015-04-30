package org.darkstorm.minecraft.darkbot.nbt;

import java.io.*;

public class NBTTagLong extends NBTBase
{
    /** The long value for the tag. */
    public long data;

    public NBTTagLong(String par1Str)
    {
        super(par1Str);
    }

    public NBTTagLong(String par1Str, long par2)
    {
        super(par1Str);
        data = par2;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeLong(data);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes
     */
    void load(DataInput par1DataInput) throws IOException
    {
        data = par1DataInput.readLong();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return 4;
    }

    public String toString()
    {
        return (new StringBuilder()).append("").append(data).toString();
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy()
    {
        return new NBTTagLong(getName(), data);
    }

    public boolean equals(Object par1Obj)
    {
        if (super.equals(par1Obj))
        {
            NBTTagLong nbttaglong = (NBTTagLong)par1Obj;
            return data == nbttaglong.data;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return super.hashCode() ^ (int)(data ^ data >>> 32);
    }
}
