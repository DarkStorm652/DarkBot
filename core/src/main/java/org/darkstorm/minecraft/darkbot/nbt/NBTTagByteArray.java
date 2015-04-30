package org.darkstorm.minecraft.darkbot.nbt;

import java.io.*;
import java.util.Arrays;

public class NBTTagByteArray extends NBTBase
{
    public byte byteArray[];

    public NBTTagByteArray(String par1Str)
    {
        super(par1Str);
    }

    public NBTTagByteArray(String par1Str, byte par2ArrayOfByte[])
    {
        super(par1Str);
        byteArray = par2ArrayOfByte;
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(byteArray.length);
        par1DataOutput.write(byteArray);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes
     */
    void load(DataInput par1DataInput) throws IOException
    {
        int i = par1DataInput.readInt();
        byteArray = new byte[i];
        par1DataInput.readFully(byteArray);
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return 7;
    }

    public String toString()
    {
        return (new StringBuilder()).append("[").append(byteArray.length).append(" bytes]").toString();
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy()
    {
        byte abyte0[] = new byte[byteArray.length];
        System.arraycopy(byteArray, 0, abyte0, 0, byteArray.length);
        return new NBTTagByteArray(getName(), abyte0);
    }

    public boolean equals(Object par1Obj)
    {
        if (super.equals(par1Obj))
        {
            return Arrays.equals(byteArray, ((NBTTagByteArray)par1Obj).byteArray);
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return super.hashCode() ^ Arrays.hashCode(byteArray);
    }
}
