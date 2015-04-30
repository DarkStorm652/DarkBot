package org.darkstorm.minecraft.darkbot.world.item;

import org.darkstorm.minecraft.darkbot.nbt.NBTTagCompound;

public interface ItemStack extends Cloneable {
	public int getId();

	public int getStackSize();

	public void setStackSize(int stackSize);

	public int getDamage();

	public void setDamage(int damage);

	public NBTTagCompound getStackTagCompound();

	public void setStackTagCompound(NBTTagCompound compound);

	public ItemStack clone();
}
