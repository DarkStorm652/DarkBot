package org.darkstorm.darkbot.minecraftbot.world.entity;

import org.darkstorm.darkbot.minecraftbot.ai.*;
import org.darkstorm.darkbot.minecraftbot.events.EventManager;
import org.darkstorm.darkbot.minecraftbot.events.protocol.client.*;
import org.darkstorm.darkbot.minecraftbot.world.*;
import org.darkstorm.darkbot.minecraftbot.world.block.*;
import org.darkstorm.darkbot.minecraftbot.world.item.*;

public class MainPlayerEntity extends PlayerEntity {
	private PlayerInventory inventory;
	private GameMode gameMode;

	private double lastX, lastY, lastZ, lastYaw, lastPitch;
	private int hunger, experienceLevel, experienceTotal;
	private Inventory window;

	public MainPlayerEntity(World world, int id, String name, GameMode gameMode) {
		super(world, id, name);
		inventory = new PlayerInventory(this);
		this.gameMode = gameMode;
	}

	public MainPlayerEntity(World world, MainPlayerEntity player) {
		this(world, player.getId(), player.getName(), player.getGameMode());

		inventory.setDelay(player.getInventory().getDelay());
	}

	public PlayerInventory getInventory() {
		return inventory;
	}

	@Override
	public ItemStack getWornItemAt(int slot) {
		return slot == 0 ? inventory.getCurrentHeldItem() : slot > 0 && slot <= 4 ? inventory.getArmorAt(slot - 1) : null;
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

	public Inventory getWindow() {
		return window;
	}

	public void setWindow(Inventory window) {
		this.window = window;
	}

	public void closeWindow() {
		if(window != null) {
			window.close();
			window = null;
		}
	}

	public void face(BlockLocation target) {
		face(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
	}

	public void face(BlockLocation target, int face) {
		BlockLocation offset = getOffsetBlock(target, face);
		if(offset == null) {
			face(target);
			return;
		}
		double x = offset.getX() + ((target.getX() - offset.getX()) / 2.0D) + 0.5;
		double y = offset.getY() + ((target.getY() - offset.getY()) / 2.0D);
		double z = offset.getZ() + ((target.getZ() - offset.getZ()) / 2.0D) + 0.5;
		face(x, y, z);
	}

	private BlockLocation getOffsetBlock(BlockLocation location, int face) {
		int x = location.getX(), y = location.getY(), z = location.getZ();
		switch(face) {
		case 0:
			return new BlockLocation(x, y + 1, z);
		case 1:
			return new BlockLocation(x, y - 1, z);
		case 2:
			return new BlockLocation(x, y, z + 1);
		case 3:
			return new BlockLocation(x, y, z - 1);
		case 4:
			return new BlockLocation(x + 1, y, z);
		case 5:
			return new BlockLocation(x - 1, y, z);
		default:
			return null;
		}
	}

	public void face(WorldLocation target) {
		face(target.getX(), target.getY(), target.getZ());
	}

	public void face(double x, double y, double z) {
		yaw = getRotationX(x, y, z);
		pitch = getRotationY(x, y, z);
	}

	private float getRotationX(double x, double y, double z) {
		double d = this.x - x;
		double d1 = this.z - z;
		return (float) (((Math.atan2(d1, d) * 180D) / Math.PI) + 90) % 360;
	}

	private float getRotationY(double x, double y, double z) {
		double dis1 = y - (this.y + 1);
		double dis2 = Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(z - this.z, 2));
		return (float) ((Math.atan2(dis2, dis1) * 180D) / Math.PI) - 90F;
	}

	public boolean isOnGround() {
		int id = world.getBlockIdAt((int) Math.floor(x), (int) Math.floor(y - 1), (int) Math.floor(z));
		return y % 1 < 0.2 && BlockType.getById(id).isSolid();
	}

	public boolean isInLiquid() {
		BlockType below = BlockType.getById(world.getBlockIdAt((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z)));
		BlockType above = BlockType.getById(world.getBlockIdAt((int) Math.floor(x), (int) Math.floor(y + 1), (int) Math.floor(z)));
		return below == BlockType.WATER || below == BlockType.LAVA || below == BlockType.STATIONARY_WATER || below == BlockType.STATIONARY_LAVA || above == BlockType.WATER || above == BlockType.LAVA || above == BlockType.STATIONARY_WATER || above == BlockType.STATIONARY_LAVA;
	}

	@Override
	public void setCrouching(boolean crouching) {
		if(crouching != this.crouching) {
			super.setCrouching(crouching);
			world.getBot().getEventManager().sendEvent(new CrouchUpdateEvent(crouching));
		}
	}

	@Override
	public void setSprinting(boolean sprinting) {
		if(sprinting != this.sprinting) {
			super.setSprinting(sprinting);
			world.getBot().getEventManager().sendEvent(new SprintUpdateEvent(sprinting));
		}
	}

	public void swingArm() {
		world.getBot().getEventManager().sendEvent(new ArmSwingEvent());
	}

	public boolean switchTools(ToolType tool) {
		MainPlayerEntity player = world.getBot().getPlayer();
		if(player == null)
			return false;
		PlayerInventory inventory = player.getInventory();

		ItemStack bestTool = null;
		int bestToolSlot = -1, bestToolValue = -1;
		for(int i = 0; i < 36; i++) {
			ItemStack item = inventory.getItemAt(i);
			if(tool == null) {
				if(i > 8)
					break;
				if(item == null || ToolType.getById(item.getId()) == null) {
					bestTool = item;
					bestToolSlot = i;
					break;
				}
				continue;
			}
			if(item == null)
				continue;
			ToolType toolType = ToolType.getById(item.getId());
			if(toolType == null || toolType != tool)
				continue;
			int toolValue = getToolPriority(item.getId());
			if(bestTool == null || toolValue > bestToolValue) {
				bestTool = item;
				bestToolSlot = i;
				bestToolValue = toolValue;
			}
		}
		if(bestToolSlot == -1)
			return false;
		return switchHeldItems(bestToolSlot);
	}

	private int getToolPriority(int id) {
		ToolType type = ToolType.getById(id);
		if(type == null)
			return 0;
		int[] ids = type.getIds();
		for(int i = 0; i < ids.length; i++)
			if(id == ids[i])
				return i + 1;
		return 0;
	}

	public boolean switchHeldItems(int newSlot) {
		if(inventory.getCurrentHeldSlot() == newSlot)
			return true;
		if(newSlot > 8) {
			int hotbarSpace = 9;
			for(int hotbarIndex = 0; hotbarIndex < 9; hotbarIndex++) {
				ItemStack item = inventory.getItemAt(hotbarIndex);
				if(item == null) {
					hotbarSpace = hotbarIndex;
					break;
				} else if(ToolType.getById(item.getId()) == null && hotbarIndex < hotbarSpace)
					hotbarSpace = hotbarIndex;
			}
			if(hotbarSpace == 9)
				hotbarSpace = 0;
			inventory.selectItemAt(newSlot);
			inventory.selectItemAt(hotbarSpace);
			if(inventory.getSelectedItem() != null)
				inventory.selectItemAt(newSlot);
			inventory.close();
			newSlot = hotbarSpace;
		}
		inventory.setCurrentHeldSlot(newSlot);
		return true;
	}

	public void hit(Entity entity) {
		EventManager eventManager = world.getBot().getEventManager();
		eventManager.sendEvent(new ArmSwingEvent());
		eventManager.sendEvent(new EntityHitEvent(entity));
	}

	public void use(Entity entity) {
		EventManager eventManager = world.getBot().getEventManager();
		eventManager.sendEvent(new ArmSwingEvent());
		eventManager.sendEvent(new EntityUseEvent(entity));
	}

	public boolean placeBlock(BlockLocation location) {
		BlockPlaceActivity activity = new BlockPlaceActivity(world.getBot(), location);
		if(activity.isActive()) {
			world.getBot().setActivity(activity);
			return true;
		}
		return false;
	}

	public boolean placeBlock(BlockLocation location, int face) {
		BlockPlaceActivity activity = new BlockPlaceActivity(world.getBot(), location, (byte) face);
		if(activity.isActive()) {
			world.getBot().setActivity(activity);
			return true;
		}
		return false;
	}

	public boolean breakBlock(BlockLocation location) {
		BlockBreakActivity activity = new BlockBreakActivity(world.getBot(), location);
		if(activity.isActive()) {
			world.getBot().setActivity(activity);
			return true;
		}
		return false;
	}

	public void walkTo(BlockLocation location) {
		world.getBot().setActivity(new WalkActivity(world.getBot(), location));
	}
}
