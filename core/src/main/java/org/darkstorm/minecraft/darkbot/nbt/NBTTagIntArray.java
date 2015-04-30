package org.darkstorm.minecraft.darkbot.nbt;

import java.io.*;
import java.util.Arrays;

public class NBTTagIntArray extends NBTBase
{
    public int field_48181_a[];

    public NBTTagIntArray(String par1Str)
    {
        super(par1Str);
    }

    public NBTTagIntArray(String par1Str, int par2ArrayOfInteger[])
    {
        super(par1Str);
        field_48181_a = par2ArrayOfInteger;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(field_48181_a.length);

        for (int i = 0; i < field_48181_a.length; i++)
        {
            par1DataOutput.writeInt(field_48181_a[i]);
        }
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes
     */
    void load(DataInput par1DataInput) throws IOException
    {
        int i = par1DataInput.readInt();
        field_48181_a = new int[i];

        for (int j = 0; j < i; j++)
        {
            field_48181_a[j] = par1DataInput.readInt();
        }
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return 11;
    }

    public String toString()
    {
        return (new StringBuilder()).append("[").append(field_48181_a.length).append(" bytes]").toString();
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy()
    {
        int ai[] = new int[field_48181_a.length];
        System.arraycopy(field_48181_a, 0, ai, 0, field_48181_a.length);
        return new NBTTagIntArray(getName(), ai);
    }

    public boolean equals(Object par1Obj)
    {
        if (super.equals(par1Obj))
        {
            NBTTagIntArray nbttagintarray = (NBTTagIntArray)par1Obj;
            return field_48181_a == null && nbttagintarray.field_48181_a == null || field_48181_a != null && field_48181_a.equals(nbttagintarray.field_48181_a);
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return super.hashCode() ^ Arrays.hashCode(field_48181_a);
    }
}
