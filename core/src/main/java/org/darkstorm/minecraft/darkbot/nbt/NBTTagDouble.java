package org.darkstorm.minecraft.darkbot.nbt;

import java.io.*;

public class NBTTagDouble extends NBTBase
{
    /** The double value for the tag. */
    public double data;

    public NBTTagDouble(String par1Str)
    {
        super(par1Str);
    }

    public NBTTagDouble(String par1Str, double par2)
    {
        super(par1Str);
        data = par2;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeDouble(data);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes
     */
    void load(DataInput par1DataInput) throws IOException
    {
        data = par1DataInput.readDouble();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return 6;
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
        return new NBTTagDouble(getName(), data);
    }

    public boolean equals(Object par1Obj)
    {
        if (super.equals(par1Obj))
        {
            NBTTagDouble nbttagdouble = (NBTTagDouble)par1Obj;
            return data == nbttagdouble.data;
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        long l = Double.doubleToLongBits(data);
        return super.hashCode() ^ (int)(l ^ l >>> 32);
    }
}
