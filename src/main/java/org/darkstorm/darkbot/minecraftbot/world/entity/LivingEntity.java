package org.darkstorm.darkbot.minecraftbot.world.entity;

import java.util.Set;

import org.darkstorm.darkbot.minecraftbot.util.IntHashMap;
import org.darkstorm.darkbot.minecraftbot.world.*;
import org.darkstorm.darkbot.minecraftbot.world.block.*;
import org.darkstorm.darkbot.minecraftbot.world.item.ItemStack;

public abstract class LivingEntity extends Entity {
	protected int health, breathTimer, growthTimer, potionEffectColor;
	protected boolean onFire, crouching, riding, sprinting, performingAction;
	protected double headYaw;

	public LivingEntity(World world, int id) {
		super(world, id);
	}
	
	@Override
	protected void move() {
		boolean inWater = isInMaterial(BlockType.WATER, BlockType.STATIONARY_WATER);
		boolean inLava = isInMaterial(BlockType.LAVA, BlockType.STATIONARY_LAVA);
		double horizFactor = 0.91;
		if(isOnGround()) {
			BlockType type = BlockType.getById(world.getBlockIdAt((int) Math.floor(x), (int) Math.floor(y - 0.1), (int) Math.floor(z)));
			if(type.isSolid())
				horizFactor *= type.getFrictionCoefficient();
		} else if(!inWater && !inLava)
			accelerate(0, -Math.PI / 2, 0.08, 3.92);
		
		velocityX *= horizFactor;
		velocityY *= 0.98;
		velocityZ *= horizFactor;
		
		if(inLava) {
			velocityX *= 0.5D;
			velocityY *= 0.5D;
			velocityZ *= 0.5D;
			velocityY -= 0.02D;
		} else if(inWater) {
			velocityX *= 0.800000011920929D;
			velocityY *= 0.800000011920929D;
			velocityZ *= 0.800000011920929D;
			velocityY -= 0.02D;
		}
		
		handleSteppingUp();
		handleCollision();
		
		super.move();
	}
	
	private void handleSteppingUp() {
		BoundingBox bounds = getBoundingBox();
		Set<Block> currentCollisions = world.getCollidingBlocks(bounds);
		if(velocityX >= -0.01 && velocityX <= 0.01 && velocityZ >= -0.01 && velocityZ <= 0.01) return;
		
		double vx = Math.signum(velocityX) * Math.min(Math.abs(velocityX), 0.05);
		double vz = Math.signum(velocityZ) * Math.min(Math.abs(velocityZ), 0.05);
		BoundingBox off = bounds.offset(vx, 0, vz);
		
		if(collides(off, currentCollisions)) {
			System.out.println("Detecting step...");
			for(int i = 1; i <= 5; i++) {
				if(!collides(off.offset(0, 0.105 * i, 0), currentCollisions)) {
					y += 0.105 * i;
					velocityY = 0.05;
					System.out.println("Detected step!");
					break;
				}
			}
		} else if(!currentCollisions.isEmpty()) {
			for(int i = 1; i <= 5; i++) {
				if(!world.isColliding(bounds.offset(0, 0.105 * i, 0))) {
					y += 0.105 * i;
					velocityY = 0.05;
					System.out.println("Detected ontop of step!");
					break;
				}
			}
		}
	}
	
	private void handleCollision() {
		BoundingBox bounds = getBoundingBox();
		Set<Block> currentCollisions = world.getCollidingBlocks(bounds);
		final double off = 0.01;
		
		double velocity = 0;
		for(double v = 0, target = Math.abs(velocityX), sign = Math.signum(velocityX);
				v < target + off && !collides(bounds.offset(sign * v, 0, 0), currentCollisions);
				v += off)
			velocity = sign * Math.min(v, target);
		velocityX = velocity;
		
		velocity = 0;
		for(double v = 0, target = Math.abs(velocityZ), sign = Math.signum(velocityZ);
				v < target + off && !collides(bounds.offset(0, 0, sign * v), currentCollisions);
				v += off)
			velocity = sign * Math.min(v, target);
		velocityZ = velocity;
		
		velocity = 0;
		for(double v = 0, target = Math.abs(velocityY), sign = Math.signum(velocityY);
				v < target + off && !collides(bounds.offset(0, sign * v, 0), currentCollisions);
				v += off)
			velocity = sign * Math.min(v, target);
		velocityY = velocity;
		
		if(collides(bounds.offset(velocityX, 0, 0), currentCollisions))
			velocityX = 0;
		if(collides(bounds.offset(0, 0, velocityZ), currentCollisions))
			velocityZ = 0;
		if(collides(bounds.offset(0, velocityY, 0), currentCollisions))
			velocityY = 0;
		if(collides(bounds.offset(velocityX, velocityY, velocityZ), currentCollisions))
			velocityX = velocityY = velocityZ = 0;
	}
	
	private boolean collides(BoundingBox bounds, Set<Block> ignore) {
		Set<Block> found = world.getCollidingBlocks(bounds);
		found.removeAll(ignore);
		return !found.isEmpty();
	}
	
	/*private boolean collides(BoundingBox bounds, BoundingBox ignore, Set<Block> ignoreBlocks) {
		Set<Block> found = world.getCollidingBlocks(bounds);
		found.removeAll(ignoreBlocks);
		found.removeAll(world.getCollidingBlocks(ignore));
		return !found.isEmpty();
	}*/

	public int getHealth() {
		return health;
	}

	public int getBreathTimer() {
		return breathTimer;
	}

	public int getGrowthTimer() {
		return growthTimer;
	}

	public int getPotionEffectColor() {
		return potionEffectColor;
	}

	public boolean isOnFire() {
		return onFire;
	}

	public boolean isCrouching() {
		return crouching;
	}

	public boolean isSprinting() {
		return sprinting;
	}

	public boolean isRiding() {
		return riding;
	}

	public boolean isPerformingAction() {
		return performingAction;
	}

	public ItemStack getWornItemAt(int slot) {
		return null;
	}

	public double getHeadYaw() {
		return headYaw;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public void setBreathTimer(int breathTimer) {
		this.breathTimer = breathTimer;
	}

	public void setGrowthTimer(int growthTimer) {
		this.growthTimer = growthTimer;
	}

	public void setPotionEffectColor(int potionEffectColor) {
		this.potionEffectColor = potionEffectColor;
	}

	public void setOnFire(boolean onFire) {
		this.onFire = onFire;
	}

	public void setCrouching(boolean crouching) {
		this.crouching = crouching;
	}

	public void setSprinting(boolean sprinting) {
		this.sprinting = sprinting;
	}

	public void setRiding(boolean riding) {
		this.riding = riding;
	}

	public void setPerformingAction(boolean performingAction) {
		this.performingAction = performingAction;
	}

	public void setWornItemAt(int slot, ItemStack item) {
	}

	public void setHeadYaw(double headYaw) {
		this.headYaw = headYaw;
	}
	
	@Override
	public BoundingBox getBoundingBoxAt(double x, double y, double z) {
		return BoundingBox.getBoundingBox(x - sizeX / 2.0, y, z - sizeZ / 2.0, x + sizeX / 2.0, y + sizeY, z + sizeZ / 2.0);
	}

	@Override
	public void updateMetadata(IntHashMap<WatchableObject> metadata) {
		if(metadata.containsKey(0)) {
			byte flags = (Byte) metadata.get(0).getObject();
			setOnFire((flags & 1) != 0);
			setCrouching((flags & 2) != 0);
			setRiding((flags & 4) != 0);
			setSprinting((flags & 8) != 0);
			setPerformingAction((flags & 16) != 0);
		}

		if(metadata.containsKey(1))
			setBreathTimer((Short) metadata.get(1).getObject());
		if(metadata.containsKey(8))
			setPotionEffectColor((Integer) metadata.get(8).getObject());
		if(metadata.containsKey(12))
			setGrowthTimer((Integer) metadata.get(12).getObject());
	}
}
