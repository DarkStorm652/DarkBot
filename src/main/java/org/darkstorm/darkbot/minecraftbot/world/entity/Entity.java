package org.darkstorm.darkbot.minecraftbot.world.entity;

import org.darkstorm.darkbot.minecraftbot.util.IntHashMap;
import org.darkstorm.darkbot.minecraftbot.world.*;
import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;

public abstract class Entity {
	protected final World world;
	protected final int id;
	protected double x, y, z, yaw, pitch;
	protected Entity rider, riding;
	protected boolean dead;

	public Entity(World world, int id) {
		this.world = world;
		this.id = id;
	}

	public World getWorld() {
		return world;
	}

	public int getId() {
		return id;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public double getYaw() {
		return yaw;
	}

	public double getPitch() {
		return pitch;
	}

	public Entity getRider() {
		return rider;
	}

	public Entity getRiding() {
		return riding;
	}

	public boolean isDead() {
		return dead;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public void setYaw(double yaw) {
		this.yaw = yaw;
	}

	public void setPitch(double pitch) {
		this.pitch = pitch;
	}

	public void setRider(Entity rider) {
		this.rider = rider;
	}

	public void setRiding(Entity riding) {
		this.riding = riding;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public WorldLocation getLocation() {
		return new WorldLocation(x, y, z);
	}

	public void setLocation(WorldLocation location) {
		x = location.getX();
		y = location.getY();
		z = location.getZ();
	}

	public void updateMetadata(IntHashMap<WatchableObject> metadata) {
	}

	public double getDistanceTo(double x, double y, double z) {
		return Math.sqrt(Math.pow(this.x - x, 2)
				+ Math.pow((this.y + 1) - y, 2) + Math.pow(this.z - z, 2));
	}

	public int getDistanceToSquared(double x, double y, double z) {
		return (int) (Math.pow(this.x - x, 2) + Math.pow((this.y + 1) - y, 2) + Math
				.pow(this.z - z, 2));
	}

	public double getDistanceTo(Entity other) {
		return getDistanceTo(other.getX(), other.getY(), other.getZ());
	}

	public int getDistanceToSquared(Entity other) {
		return getDistanceToSquared(other.getX(), other.getY(), other.getZ());
	}

	public double getDistanceTo(WorldLocation location) {
		return getDistanceTo(location.getX(), location.getY(), location.getZ());
	}

	public double getDistanceToSquared(WorldLocation location) {
		return getDistanceTo(location.getX(), location.getY(), location.getZ());
	}

	public double getDistanceTo(BlockLocation location) {
		return getDistanceTo(location.getX(), location.getY(), location.getZ());
	}

	public double getDistanceToSquared(BlockLocation location) {
		return getDistanceTo(location.getX(), location.getY(), location.getZ());
	}
}
