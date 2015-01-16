package org.darkstorm.darkbot.minecraftbot.world.entity;

import java.util.*;

import org.darkstorm.darkbot.minecraftbot.ai.*;
import org.darkstorm.darkbot.minecraftbot.event.EventBus;
import org.darkstorm.darkbot.minecraftbot.event.protocol.client.*;
import org.darkstorm.darkbot.minecraftbot.world.*;
import org.darkstorm.darkbot.minecraftbot.world.block.*;
import org.darkstorm.darkbot.minecraftbot.world.item.*;

public class MainPlayerEntity extends PlayerEntity {
	private PlayerInventory inventory;
	private GameMode gameMode;

	private double lastX, lastY, lastZ, lastYaw, lastPitch;
	private int hunger, experienceLevel, experienceTotal;
	private Inventory window;
	
	private boolean enforcingCollision = true;

	public MainPlayerEntity(World world, int id, String name, GameMode gameMode) {
		super(world, id, name);
		
		inventory = new PlayerInventory(this);
		this.gameMode = gameMode;
	}

	public MainPlayerEntity(World world, MainPlayerEntity player) {
		this(world, player.getId(), player.getName(), player.getGameMode());

		inventory.setDelay(player.getInventory().getDelay());
	}
	
	@Override
	public void update() {
		move();
		
		super.update();
	}
	
	@Override
	protected void move() {
		lastX = x;
		lastY = y;
		lastZ = z;
		lastYaw = yaw;
		lastPitch = pitch;
		
		//enforcingCollision = Boolean.FALSE;
		if(!enforcingCollision)
			return;
		
		BoundingBox bounds = getBoundingBox();
		handlePushingOutOfBlocks(bounds);
		if(isOnGround() && isCrouching())
			handleSneaking(bounds);
		
		handlePlayerPushing(bounds);
		
		super.move();
		
		//System.out.printf("Vel: <%.9f, %.9f, %.9f> Pos: <%.5f, %.5f, %.5f> Colliding: %s%n", velocityX, velocityY, velocityZ, x, y, z, world.isColliding(bounds) ? "YES" : "NO");
	}
	
	private void handleSneaking(BoundingBox bounds) {
		Set<Block> currentCollisions = world.getCollidingBlocks(bounds);
		final double off = 0.1;

		double velocity = 0;
		for(double v = 0, target = Math.abs(velocityX), sign = Math.signum(velocityX);
				v < target + off && collides(bounds.offset(sign * v, -0.1, 0), currentCollisions);
				v += off)
			velocity = sign * Math.min(v, target);
		velocityX = velocity;
		
		velocity = 0;
		for(double v = 0, target = Math.abs(velocityZ), sign = Math.signum(velocityZ);
				v < target + off && collides(bounds.offset(0, -0.1, sign * v), currentCollisions);
				v += off)
			velocity = sign * Math.min(v, target);
		velocityZ = velocity;
	}
	
	private boolean collides(BoundingBox bounds, Set<Block> ignore) {
		Set<Block> found = world.getCollidingBlocks(bounds);
		found.removeAll(ignore);
		return !found.isEmpty();
	}
	
	private void handlePushingOutOfBlocks(BoundingBox bounds) {
		Set<Block> colliding = world.getCollidingBlocks(bounds);
		if(!colliding.isEmpty()) {
			final WorldLocation location = getLocation();
			Set<BlockLocation> checks = new TreeSet<BlockLocation>(new Comparator<BlockLocation>() {
				public int compare(BlockLocation loc1, BlockLocation loc2) {
					return loc1.equals(loc2) ? 0 : distSq(loc1) < distSq(loc2) ? -1 : 1;
				}
				
				private double distSq(BlockLocation loc) {
					return Math.pow(location.getX() - (loc.getX() + 0.5), 2) + Math.pow(location.getY() - loc.getY(), 2) + Math.pow(location.getZ() - (loc.getZ() + 0.5), 2);
				}
			});
			
			int floorY = (int) Math.floor(getY());
			for(Block block : colliding) {
				BlockLocation loc = block.getLocation();
				checks.add(new BlockLocation(loc.getX() + 1, floorY, loc.getZ()));
				checks.add(new BlockLocation(loc.getX() - 1, floorY, loc.getZ()));
				checks.add(new BlockLocation(loc.getX(), floorY, loc.getZ() + 1));
				checks.add(new BlockLocation(loc.getX(), floorY, loc.getZ() - 1));
			}
			for(Block block : colliding) {
				BlockLocation loc = block.getLocation();
				checks.remove(new BlockLocation(loc.getX(), floorY, loc.getZ()));
				checks.remove(loc);
			}
			
			BlockLocation target = null;
			for(BlockLocation check : checks) {
				if(!world.isColliding(getBoundingBoxAt(check.getX() + 0.5, y, check.getZ() + 0.5))) {
					target = check;
					break;
				}
			}
			if(target == null)
				return;
			
			double angle = Math.atan2((target.getZ() + 0.5) - z, (target.getX() + 0.5) - x);
			accelerate(angle, 0, 0.04, 0.05);
			
			System.out.println("Trying to get out of " + new BlockLocation(location) + " and toward " + target);
		}
	}
	
	private void handlePlayerPushing(BoundingBox bounds) {
		for(Entity entity : world.getEntities()) {
			if(entity instanceof PlayerEntity && this != entity) {
				BoundingBox otherBounds = entity.getBoundingBox();
				if(!bounds.intersectsWith(otherBounds))
					continue;
				
				double angle = Math.atan2(z - entity.z, x - entity.x);
				accelerate(angle, 0, 0.025, 0.05);
				
				System.out.println("Being pushed by " + ((PlayerEntity) entity).getName());
			}
		}
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
	
	public boolean isEnforcingCollision() {
		return enforcingCollision;
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
	
	public void setEnforcingCollision(boolean enforcingCollision) {
		this.enforcingCollision = enforcingCollision;
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

	@Override
	public void setCrouching(boolean crouching) {
		if(crouching != this.crouching) {
			super.setCrouching(crouching);
			world.getBot().getEventBus().fire(new CrouchUpdateEvent(crouching));
		}
	}

	@Override
	public void setSprinting(boolean sprinting) {
		if(sprinting != this.sprinting) {
			super.setSprinting(sprinting);
			world.getBot().getEventBus().fire(new SprintUpdateEvent(sprinting));
		}
	}

	public void swingArm() {
		world.getBot().getEventBus().fire(new ArmSwingEvent());
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
		EventBus eventBus = world.getBot().getEventBus();
		eventBus.fire(new ArmSwingEvent());
		eventBus.fire(new EntityHitEvent(entity));
	}

	public void use(Entity entity) {
		EventBus eventBus = world.getBot().getEventBus();
		eventBus.fire(new ArmSwingEvent());
		eventBus.fire(new EntityUseEvent(entity));
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
