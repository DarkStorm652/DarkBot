package org.darkstorm.darkbot.minecraftbot.ai;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.protocol.ConnectionHandler;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.*;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.Packet18Animation.Animation;
import org.darkstorm.darkbot.minecraftbot.protocol.writeable.Packet7UseEntity;
import org.darkstorm.darkbot.minecraftbot.util.Util;
import org.darkstorm.darkbot.minecraftbot.world.World;
import org.darkstorm.darkbot.minecraftbot.world.block.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.*;
import org.darkstorm.darkbot.minecraftbot.world.item.*;

public class AttackTask implements Task {
	private static final int[] SWORDS = new int[3200];

	private final MinecraftBot bot;

	private Entity attackEntity;

	private int attackCooldown = 0;

	static {
		SWORDS[268] = 1;
		SWORDS[272] = 2;
		SWORDS[276] = 4;
		SWORDS[283] = 3;
	}

	public AttackTask(MinecraftBot bot) {
		this.bot = bot;
	}

	public Entity getAttackEntity() {
		return attackEntity;
	}

	public void setAttackEntity(Entity attackEntity) {
		this.attackEntity = attackEntity;
	}

	@Override
	public synchronized boolean isPreconditionMet() {
		return attackEntity != null;
	}

	@Override
	public synchronized boolean start(String... options) {
		if(options.length > 0) {
			String name = options[0];
			World world = bot.getWorld();
			if(world == null)
				return false;
			for(Entity entity : world.getEntities())
				if(entity instanceof PlayerEntity && name.equalsIgnoreCase(Util.stripColors(((PlayerEntity) entity).getName())))
					attackEntity = entity;
		}
		if(attackEntity == null)
			return false;
		attackCooldown = 5;
		return true;
	}

	@Override
	public synchronized void stop() {
		attackEntity = null;
	}

	@Override
	public synchronized void run() {
		MainPlayerEntity player = bot.getPlayer();
		if(player == null)
			return;
		if(player.getDistanceTo(attackEntity) > 4) {
			BlockLocation location = new BlockLocation(attackEntity.getLocation());
			World world = bot.getWorld();
			BlockLocation original = location;
			BlockLocation below = location.offset(0, -1, 0);
			while(!BlockType.getById(world.getBlockIdAt(below)).isSolid() && !world.getPathFinder().getHeuristic().isClimbableBlock(below)) {
				location = below;
				below = below.offset(0, -1, 0);
				if(original.getY() - location.getY() >= 5)
					return;
			}
			bot.setActivity(new WalkActivity(bot, location, true));
			return;
		} else {
			if(attackCooldown > 0) {
				attackCooldown--;
				return;
			}
			ConnectionHandler connectionHandler = bot.getConnectionHandler();
			if(!bot.getTaskManager().getTaskFor(EatTask.class).isActive())
				switchToBestSword();
			connectionHandler.sendPacket(new Packet18Animation(player.getId(), Animation.SWING_ARM));
			connectionHandler.sendPacket(new Packet7UseEntity(player.getId(), attackEntity.getId(), 1));
			attackCooldown = 5;
		}
	}

	private void switchToBestSword() {
		MainPlayerEntity player = bot.getPlayer();
		if(player == null)
			return;
		PlayerInventory inventory = player.getInventory();
		ItemStack bestTool = null;
		int bestToolSlot = -1, bestToolValue = -1;
		for(int i = 0; i < 36; i++) {
			ItemStack item = inventory.getItemAt(i);
			if(item != null) {
				int toolValue = SWORDS[item.getId()];
				if(bestTool != null ? toolValue > bestToolValue : toolValue > 0) {
					bestTool = item;
					bestToolSlot = i;
					bestToolValue = toolValue;
				}
			}
		}
		if(bestTool != null) {
			if(inventory.getCurrentHeldSlot() != bestToolSlot) {
				if(bestToolSlot > 8) {
					int hotbarSpace = 9;
					for(int hotbarIndex = 0; hotbarIndex < 9; hotbarIndex++) {
						if(inventory.getItemAt(hotbarIndex) == null) {
							hotbarSpace = hotbarIndex;
							break;
						}
					}
					if(hotbarSpace == 9)
						return;
					inventory.selectItemAt(bestToolSlot);
					inventory.selectItemAt(hotbarSpace);
					if(inventory.getSelectedItem() != null)
						inventory.selectItemAt(bestToolSlot);
					inventory.close();
					bestToolSlot = hotbarSpace;
				}
				inventory.setCurrentHeldSlot(bestToolSlot);
			}
		}
	}

	@Override
	public synchronized boolean isActive() {
		boolean active = attackEntity != null && !attackEntity.isDead();
		if(active) {
			MainPlayerEntity player = bot.getPlayer();
			if(player == null)
				return true;
			player.face(attackEntity.getX(), attackEntity.getY() + 1.5, attackEntity.getZ());
			Activity activity = bot.getActivity();
			if(activity == null || !(activity instanceof WalkActivity))
				return active;
			WalkActivity walkActivity = (WalkActivity) activity;
			if(walkActivity.isActive() && (player.getDistanceTo(attackEntity) < 3 || attackEntity.getDistanceTo(walkActivity.getTarget()) > 3) && player.isOnGround())
				bot.setActivity(null);
		}
		return active;
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
		return "Attack";
	}

	@Override
	public String getOptionDescription() {
		return "[player]";
	}
}
