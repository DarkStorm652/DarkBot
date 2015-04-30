package org.darkstorm.minecraft.darkbot.ai;

import org.darkstorm.minecraft.darkbot.MinecraftBot;
import org.darkstorm.minecraft.darkbot.event.*;
import org.darkstorm.minecraft.darkbot.event.protocol.client.*;
import org.darkstorm.minecraft.darkbot.event.protocol.server.EntityVelocityEvent;
import org.darkstorm.minecraft.darkbot.world.*;
import org.darkstorm.minecraft.darkbot.world.block.BlockLocation;
import org.darkstorm.minecraft.darkbot.world.entity.*;
import org.darkstorm.minecraft.darkbot.world.item.*;

public class FishingTask implements Task, EventListener {
	private final MinecraftBot bot;

	private boolean running = false;
	private boolean fishing = false;
	private int ticksFished = 0;

	public FishingTask(final MinecraftBot bot) {
		this.bot = bot;
		bot.getEventBus().register(this);
	}

	@Override
	public synchronized boolean isPreconditionMet() {
		return running;
	}

	@Override
	public synchronized boolean start(String... options) {
		running = true;
		return true;
	}

	@Override
	public synchronized void stop() {
		running = false;
		if(fishing) {
			if(!switchToFishingRod()) {
				stop();
				return;
			}
			useFishingRod();
		}
		fishing = false;
	}

	@Override
	public synchronized void run() {
		if(fishing) {
			ticksFished++;
			if(ticksFished > 2000) {
				useFishingRod();
				fishing = false;
			}
			return;
		}
		ticksFished = 0;
		System.out.println("Fishing!");
		if(!switchToFishingRod()) {
			stop();
			return;
		}
		BlockLocation closest = getClosestWater();
		System.out.println("Fishing at: " + closest);
		if(closest == null)
			return;
		MainPlayerEntity player = bot.getPlayer();
		if(player == null)
			return;
		WorldLocation target = new WorldLocation(closest.offset(0, 1, 0));
		if(player.getDistanceToSquared(target) < 25) {
			player.face(target);
			EventBus eventBus = bot.getEventBus();
			eventBus.fire(new PlayerRotateEvent(player));
			useFishingRod();
			fishing = true;
		}
	}

	private void useFishingRod() {
		MainPlayerEntity player = bot.getPlayer();
		EventBus eventBus = bot.getEventBus();
		eventBus.fire(new ArmSwingEvent());
		eventBus.fire(new ItemUseEvent(player.getInventory().getCurrentHeldItem()));
	}

	@Override
	public synchronized boolean isActive() {
		return running;
	}

	private BlockLocation getClosestWater() {
		MainPlayerEntity player = bot.getPlayer();
		World world = bot.getWorld();
		if(player == null || world == null)
			return null;
		BlockLocation ourLocation = new BlockLocation(player.getLocation());
		int radius = 8;
		int closestX = 0, closestY = 0, closestZ = 0, closestDistance = Integer.MAX_VALUE;
		for(int x = -radius; x < radius; x++) {
			for(int y = -radius; y < radius; y++) {
				for(int z = -radius; z < radius; z++) {
					int id = world.getBlockIdAt(ourLocation.getX() + x, ourLocation.getY() + y, ourLocation.getZ() + z);
					int distance = player.getDistanceToSquared(ourLocation.getX() + x, ourLocation.getY() + y, ourLocation.getZ() + z);
					if((id == 8 || id == 9) && distance < closestDistance) {
						closestX = ourLocation.getX() + x;
						closestY = ourLocation.getY() + y;
						closestZ = ourLocation.getZ() + z;
						closestDistance = distance;
					}
				}
			}
		}
		return closestDistance < Integer.MAX_VALUE ? new BlockLocation(closestX, closestY, closestZ) : null;
	}

	@EventHandler
	public void onEntityVelocity(EntityVelocityEvent event) {
		World world = bot.getWorld();
		if(world == null)
			return;
		Entity entity = world.getEntityById(event.getEntityId());
		if(entity == null || !(entity instanceof FishingBobEntity) || !(((FishingBobEntity) entity).getThrower() instanceof MainPlayerEntity))
			return;
		System.out.println("Fishing bob velocity!");
		if(event.getVelocityX() == 0 && event.getVelocityY() < 0 && event.getVelocityZ() == 0) {
			if(fishing) {
				if(!switchToFishingRod()) {
					stop();
					return;
				}
				useFishingRod();
			}
			fishing = false;
		}
	}

	private boolean switchToFishingRod() {
		MainPlayerEntity player = bot.getPlayer();
		if(player == null)
			return false;
		PlayerInventory inventory = player.getInventory();
		int slot = -1;
		for(int i = 0; i < 36; i++) {
			ItemStack item = inventory.getItemAt(i);
			if(item == null)
				continue;
			int id = item.getId();
			if(id == 346) {
				slot = i;
				break;
			}
		}
		if(slot == -1)
			return false;
		if(inventory.getCurrentHeldSlot() != slot) {
			if(slot > 8) {
				int hotbarSpace = 9;
				for(int hotbarIndex = 0; hotbarIndex < 9; hotbarIndex++) {
					if(inventory.getItemAt(hotbarIndex) == null) {
						hotbarSpace = hotbarIndex;
						break;
					} else if(hotbarIndex < hotbarSpace)
						hotbarSpace = hotbarIndex;
				}
				if(hotbarSpace == 9)
					return false;
				inventory.selectItemAt(slot);
				inventory.selectItemAt(hotbarSpace);
				if(inventory.getSelectedItem() != null)
					inventory.selectItemAt(slot);
				inventory.close();
				slot = hotbarSpace;
			}
			inventory.setCurrentHeldSlot(slot);
		}
		return true;
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
		return "Fish";
	}

	@Override
	public String getOptionDescription() {
		return "";
	}
}
