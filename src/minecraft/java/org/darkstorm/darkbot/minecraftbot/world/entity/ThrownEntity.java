package org.darkstorm.darkbot.minecraftbot.world.entity;

import org.darkstorm.darkbot.minecraftbot.world.World;

public abstract class ThrownEntity extends Entity {
	private Entity thrower;

	public ThrownEntity(World world, int id) {
		super(world, id);
	}

	public Entity getThrower() {
		return thrower;
	}

	public void setThrower(Entity thrower) {
		this.thrower = thrower;
	}
}
