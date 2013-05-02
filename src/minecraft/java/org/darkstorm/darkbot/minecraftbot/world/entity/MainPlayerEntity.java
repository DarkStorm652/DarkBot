package org.darkstorm.darkbot.minecraftbot.world.entity;

import org.darkstorm.darkbot.minecraftbot.handlers.ConnectionHandler;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.*;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.Packet18Animation.Animation;
import org.darkstorm.darkbot.minecraftbot.world.*;
import org.darkstorm.darkbot.minecraftbot.world.block.BlockType;
import org.darkstorm.darkbot.minecraftbot.world.item.*;

public class MainPlayerEntity extends PlayerEntity {
	private PlayerInventory inventory;
	private GameMode gameMode;

	private double lastX, lastY, lastZ, lastYaw, lastPitch;

	private int hunger, experienceLevel, experienceTotal;

	public MainPlayerEntity(World world, int id, String name, GameMode gameMode) {
		super(world, id, name);
		inventory = new PlayerInventory(this);
		this.gameMode = gameMode;
	}

	public PlayerInventory getInventory() {
		return inventory;
	}

	@Override
	public ItemStack getWornItemAt(int slot) {
		return slot == 0 ? inventory.getCurrentHeldItem() : slot > 0
				&& slot <= 4 ? inventory.getArmorAt(slot - 1) : null;
	}

	public GameMode getGameMode() {
		return gameMode;
	}

	public double getLastX() {
		return lastX;
	}

	public double getLastY() {
		return lastY;
	}

	public double getLastZ() {
		return lastZ;
	}

	public double getLastYaw() {
		return lastYaw;
	}

	public double getLastPitch() {
		return lastPitch;
	}

	public int getHunger() {
		return hunger;
	}

	public int getExperienceLevel() {
		return experienceLevel;
	}

	public int getExperienceTotal() {
		return experienceTotal;
	}

	public int getExperience() {
		return experienceTotal - getExperienceForLevel(experienceLevel);
	}

	public int getExperienceToNextLevel() {
		return getExperienceForLevel(experienceLevel + 1) - experienceTotal;
	}

	public int getExperienceForLevel(int level) {
		if(level <= 15)
			return 17 * level;
		else if(level <= 30)
			return (int) ((1.5 * Math.pow(level, 2)) - (29.5 * level) + 360);
		else
			return (int) ((3.5 * Math.pow(level, 2)) - (151.5 * level) + 2220);
	}

	@Override
	public void setWornItemAt(int slot, ItemStack item) {
		if(slot == 0)
			inventory.setItemAt(inventory.getCurrentHeldSlot(), item);
		else if(slot > 0 && slot <= 4)
			inventory.setArmorAt(slot - 1, item);
	}

	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	public void setLastX(double lastX) {
		this.lastX = lastX;
	}

	public void setLastY(double lastY) {
		this.lastY = lastY;
	}

	public void setLastZ(double lastZ) {
		this.lastZ = lastZ;
	}

	public void setLastYaw(double lastYaw) {
		this.lastYaw = lastYaw;
	}

	public void setLastPitch(double lastPitch) {
		this.lastPitch = lastPitch;
	}

	public void setHunger(int hunger) {
		this.hunger = hunger;
	}

	public void setExperienceLevel(int experienceLevel) {
		this.experienceLevel = experienceLevel;
	}

	public void setExperienceTotal(int experienceTotal) {
		this.experienceTotal = experienceTotal;
	}

	public void face(double x, double y, double z) {
		yaw = getRotationX(x, y, z);
		pitch = getRotationY(x, y, z);
	}

	private float getRotationX(double x, double y, double z) {
		double d = this.x - (x + 0.5);
		double d1 = this.z - (z + 0.5);
		return (float) (((Math.atan2(d1, d) * 180D) / Math.PI) + 90) % 360;
	}

	private float getRotationY(double x, double y, double z) {
		double dis1 = y - (this.y + 1);
		double dis2 = Math.sqrt(Math.pow((x + 0.5) - (this.x), 2)
				+ Math.pow((z + 0.5) - (this.z), 2));
		return (float) ((Math.atan2(dis2, dis1) * 180D) / Math.PI) - 90F;
	}

	public boolean isOnGround() {
		return y % 1 < 0.2
				&& BlockType.getById(
						world.getBlockIdAt((int) (x - 0.5), (int) (y - 1),
								(int) (z - 0.5))).isSolid();
	}

	public boolean isInLiquid() {
		BlockType below = BlockType.getById(world.getBlockIdAt((int) (x - 0.5),
				(int) (y), (int) (z - 0.5)));
		BlockType above = BlockType.getById(world.getBlockIdAt((int) (x - 0.5),
				(int) (y + 1), (int) (z - 0.5)));
		return below == BlockType.WATER || below == BlockType.LAVA
				|| below == BlockType.STATIONARY_WATER
				|| below == BlockType.STATIONARY_LAVA
				|| above == BlockType.WATER || above == BlockType.LAVA
				|| above == BlockType.STATIONARY_WATER
				|| above == BlockType.STATIONARY_LAVA;
	}

	public void swingArm() {
		ConnectionHandler handler = world.getBot().getConnectionHandler();
		handler.sendPacket(new Packet18Animation(getId(), Animation.SWING_ARM));
	}
}
