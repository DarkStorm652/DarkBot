package org.darkstorm.minecraft.darkbot.world.item;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;

public class BasicItemStack implements ItemStack {
	private int id, stackSize, damage;
	private CompoundTag stackTagCompound;

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

	public CompoundTag getStackTagCompound() {
		return stackTagCompound;
	}

	public void setStackTagCompound(CompoundTag stackTagCompound) {
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
