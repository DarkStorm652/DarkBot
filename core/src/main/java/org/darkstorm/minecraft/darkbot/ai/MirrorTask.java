package org.darkstorm.minecraft.darkbot.ai;

import org.darkstorm.minecraft.darkbot.*;
import org.darkstorm.minecraft.darkbot.util.ChatColor;
import org.darkstorm.minecraft.darkbot.world.*;
import org.darkstorm.minecraft.darkbot.world.entity.*;
import org.darkstorm.minecraft.darkbot.world.item.*;

public class MirrorTask implements Task {
	public static interface Filter {
		public boolean canAttack(LivingEntity entity);
	}
	public static enum RotationMode {
		TRACK, ORBIT
	}
	
	private final MinecraftBot bot;
	private LivingEntity mirroring = null;
	private boolean started = false;
	private double offR, offD, rotateD;
	private int offY;
	private int attackCooldown = 0;
	
	private Filter filter;
	private RotationMode mode = RotationMode.TRACK;

	public MirrorTask(MinecraftBot bot) {
		this.bot = bot;
	}

	public void mirror(LivingEntity entity) {
		mirroring = entity;
	}
	
	public void filter(Filter filter) {
		this.filter = filter;
	}
	
	public void setRotationMode(RotationMode mode) {
		this.mode = mode;
	}

	@Override
	public synchronized boolean isPreconditionMet() {
		return mirroring != null;
	}

	@Override
	public synchronized boolean start(String... options) {
		if(options.length > 0) {
			String name = options[0];
			World world = bot.getWorld();
			if(world == null)
				return false;
			for(Entity entity : world.getEntities())
				if(entity instanceof PlayerEntity && name.equalsIgnoreCase(ChatColor.stripColor(((PlayerEntity) entity).getName())))
					mirroring = (LivingEntity) entity;
		}
		if(mirroring != null) {
			double x = mirroring.getX() - bot.getPlayer().getX(), z = mirroring.getZ() - bot.getPlayer().getZ();
			offR = Math.hypot(x, z);
			offD = Math.atan2(z, x) - Math.toRadians(mirroring.getHeadYaw());
			offY = (int) Math.floor(mirroring.getY() - bot.getPlayer().getY());
			attackCooldown = 5;
			started = true;
			return true;
		}
		return false;
	}

	@Override
	public void stop() {
		mirroring = null;
		started = false;
	}

	@Override
	public void run() {
		mode = RotationMode.ORBIT;
		MainPlayerEntity player = bot.getPlayer();
		if(mirroring == null || player == null)
			return;
		
		if(mirroring.isDead()) {
			if(mirroring instanceof PlayerEntity) {
				String name = ((PlayerEntity) mirroring).getName();
				for(Entity entity : player.getWorld().getEntities()) {
					if(entity instanceof PlayerEntity && name.equalsIgnoreCase(ChatColor.stripColor(((PlayerEntity) entity).getName()))) {
						mirroring = (LivingEntity) entity;
						break;
					}
				}
			} else {
				stop();
				return;
			}
		}
		
		player.setYaw(mirroring.getYaw());
		player.setPitch(mirroring.getPitch());
		
		
		double speed = 0.17;
		double d = offD;
		if(mode == RotationMode.TRACK)
			d += Math.toRadians(mirroring.getHeadYaw());
		else
			d += Math.toRadians(rotateD = (rotateD + 3) % 360);
		
		double x = -(offR * Math.cos(d) - mirroring.getX()) - player.getX();
		double z = -(offR * Math.sin(d) - mirroring.getZ()) - player.getZ();
		double dist = Math.hypot(x, z);
		if(dist < 0.5) {
			player.setVelocityX(0);
			player.setVelocityZ(0);
			player.accelerate(Math.atan2(z, x), 0, Math.min(dist / 4, speed / 4), Math.min(dist, speed));
		} else
			player.accelerate(Math.atan2(z, x), 0, speed / 4, Math.min(dist, speed));
		
		BoundingBox bounds = player.getBoundingBox().offset(Math.min(0.01, Math.abs(player.getVelocityX())) * Math.signum(player.getVelocityX()),
		                                                    0,
		                                                    Math.min(0.01, Math.abs(player.getVelocityZ())) * Math.signum(player.getVelocityZ()));
		if(player.getWorld().isColliding(bounds))
			player.jump();
		
		if(mirroring instanceof PlayerEntity) {
			ItemStack item = mirroring.getWornItemAt(0);
			int id = 0;
			if(item != null)
				id = item.getId();
			
			int slot = player.getInventory().getFirstSlot(id);
			if(slot >= 0 && slot < 9 && slot != player.getInventory().getCurrentHeldSlot())
				player.getInventory().setCurrentHeldSlot(slot);
		}
		
		Entity attackTarget = null;
		for(Entity entity : player.getWorld().getEntities()) {
			if(entity != player && entity instanceof LivingEntity && !entity.equals(mirroring) && player.getDistanceTo(entity) < 4.5) {
				if(filter != null && !filter.canAttack((LivingEntity) entity))
					continue;
				if(entity instanceof PlayerEntity) {
					ItemStack item = ((PlayerEntity) entity).getWornItemAt(1);
					if(item == null || item.getId() == 0)
						continue;
				}
					
				attackTarget = entity;
				break;
			}
		}
		if(attackTarget != null)
			player.face(attackTarget.getX(), attackTarget.getY() + 1, attackTarget.getZ());
		if(attackCooldown > 0) {
			attackCooldown--;
			return;
		}
		if(attackTarget != null) {
			player.hit(attackTarget);
			attackCooldown = 5;
		}
	}

	@Override
	public boolean isActive() {
		return started && mirroring != null;
	}

	@Override
	public TaskPriority getPriority() {
		return TaskPriority.NORMAL;
	}

	@Override
	public boolean isExclusive() {
		return false;
	}

	@Override
	public boolean ignoresExclusive() {
		return false;
	}

	@Override
	public String getName() {
		return "Mirror";
	}

	@Override
	public String getOptionDescription() {
		return "[player]";
	}
}
