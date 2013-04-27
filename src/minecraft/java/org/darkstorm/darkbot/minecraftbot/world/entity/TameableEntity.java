package org.darkstorm.darkbot.minecraftbot.world.entity;

import org.darkstorm.darkbot.minecraftbot.world.World;

public abstract class TameableEntity extends LivingEntity {
	protected String ownerName;
	protected boolean sitting, aggressive, tamed;

	public TameableEntity(World world, int id) {
		super(world, id);
	}

	public String getOwnerName() {
		return ownerName;
	}

	public boolean isSitting() {
		return sitting;
	}

	public boolean isAggressive() {
		return aggressive;
	}

	public boolean isTamed() {
		return tamed;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public void setSitting(boolean sitting) {
		this.sitting = sitting;
	}

	public void setAggressive(boolean aggressive) {
		this.aggressive = aggressive;
	}

	public void setTamed(boolean tamed) {
		this.tamed = tamed;
	}
}
