package org.darkstorm.darkbot.minecraftbot.nbt;

import java.io.*;

public class NBTTagShort extends NBTBase
{
    /** The short value for the tag. */
    public short data;

    public NBTTagShort(String par1Str)
    {
        super(par1Str);
    }

    public NBTTagShort(String par1Str, short par2)
    {
        super(par1Str);
        data = par2;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeShort(data);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes
     */
    void load(DataInput par1DataInput) throws IOException
    {
        data = par1DataInput.readShort();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return 2;
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
        return new NBTTagShort(getName(), data);
    }

    public boolean equals(Object par1Obj)
    {
        if (super.equals(par1Obj))
        {
            NBTTagShort nbttagshort = (NBTTagShort)par1Obj;
            return data == nbttagshort.data;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return super.hashCode() ^ data;
    }
}
