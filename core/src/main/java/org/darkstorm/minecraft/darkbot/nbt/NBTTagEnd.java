package org.darkstorm.minecraft.darkbot.nbt;

import java.io.*;

public class NBTTagEnd extends NBTBase
{
    public NBTTagEnd()
    {
        super(null);
    }

    /**
     * Read the actual data contents of the tag, implemented in NBT extension classes
     */
    void load(DataInput datainput) throws IOException
    {
    }

    /**
     * Write the actual data contents of the tag, implemented in NBT extension classes
     */
    void write(DataOutput dataoutput) throws IOException
    {
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId()
    {
        return 0;
    }

    public String toString()
    {
        return "END";
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy()
    {
        return new NBTTagEnd();
    }

    public boolean equals(Object par1Obj)
    {
        return super.equals(par1Obj);
    }
}
