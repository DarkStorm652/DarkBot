package org.darkstorm.minecraft.darkbot.world.item;

import org.darkstorm.minecraft.darkbot.nbt.NBTTagCompound;

public class BasicItemStack implements ItemStack {
	private int id, stackSize, damage;
	private NBTTagCompound stackTagCompound;

	public BasicItemStack(int id, int stackSize, int damage) {
		this.id = id;
		this.stackSize = stackSize;
		this.damage = damage;
	}

	public int getId() {
		return id;
	}

	public int getStackSize() {
		return stackSize;
	}

	public void setStackSize(int stackSize) {
		this.stackSize = stackSize;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public NBTTagCompound getStackTagCompound() {
		return stackTagCompound;
	}

	public void setStackTagCompound(NBTTagCompound stackTagCompound) {
		this.stackTagCompound = stackTagCompound;
	}

	@Override
	public ItemStack clone() {
		return new BasicItemStack(id, stackSize, damage);
	}

	@Override
	public String toString() {
		return "ItemStack[" + id + ":" + damage + "x" + stackSize + "]";
	}
}
