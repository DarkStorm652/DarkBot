package org.darkstorm.darkbot.minecraftbot.ai;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.events.*;
import org.darkstorm.darkbot.minecraftbot.events.io.PacketProcessEvent;
import org.darkstorm.darkbot.minecraftbot.handlers.ConnectionHandler;
import org.darkstorm.darkbot.minecraftbot.protocol.Packet;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.*;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.Packet18Animation.Animation;
import org.darkstorm.darkbot.minecraftbot.protocol.readable.Packet28EntityVelocity;
import org.darkstorm.darkbot.minecraftbot.protocol.writeable.*;
import org.darkstorm.darkbot.minecraftbot.world.World;
import org.darkstorm.darkbot.minecraftbot.world.block.BlockLocation;
import org.darkstorm.darkbot.minecraftbot.world.entity.*;
import org.darkstorm.darkbot.minecraftbot.world.item.*;

public class FishingTask implements Task, EventListener {
	private final MinecraftBot bot;

	private boolean running = false;
	private boolean fishing = false;
	private int ticksFished = 0;

	public FishingTask(final MinecraftBot bot) {
		this.bot = bot;
		bot.getEventManager().registerListener(this);
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
			MainPlayerEntity player = bot.getPlayer();
			ConnectionHandler connectionHandler = bot.getConnectionHandler();
			connectionHandler.sendPacket(new Packet18Animation(player.getId(),
					Animation.SWING_ARM));
			Packet15Place placePacket = new Packet15Place();
			placePacket.xPosition = -1;
			placePacket.yPosition = -1;
			placePacket.zPosition = -1;
			placePacket.direction = 255;
			placePacket.itemStack = player.getInventory().getCurrentHeldItem();
			connectionHandler.sendPacket(placePacket);
		}
		fishing = false;
	}

	@Override
	public synchronized void run() {
		if(fishing) {
			ticksFished++;
			if(ticksFished > 2000) {
				MainPlayerEntity player = bot.getPlayer();
				ConnectionHandler connectionHandler = bot
						.getConnectionHandler();
				connectionHandler.sendPacket(new Packet18Animation(player
						.getId(), Animation.SWING_ARM));
				Packet15Place placePacket = new Packet15Place();
				placePacket.xPosition = -1;
				placePacket.yPosition = -1;
				placePacket.zPosition = -1;
				placePacket.direction = 255;
				placePacket.itemStack = player.getInventory()
						.getCurrentHeldItem();
				connectionHandler.sendPacket(placePacket);
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
		if(player.getDistanceToSquared(closest.getX(), closest.getY() + 1,
				closest.getZ()) < 25) {
			player.face(closest.getX(), closest.getY() + 1, closest.getZ());
			ConnectionHandler connectionHandler = bot.getConnectionHandler();
			connectionHandler.sendPacket(new Packet12PlayerLook((float) player
					.getYaw(), (float) player.getPitch(), true));
			connectionHandler.sendPacket(new Packet18Animation(player.getId(),
					Animation.SWING_ARM));
			Packet15Place placePacket = new Packet15Place();
			placePacket.xPosition = -1;
			placePacket.yPosition = -1;
			placePacket.zPosition = -1;
			placePacket.direction = 255;
			placePacket.itemStack = player.getInventory().getCurrentHeldItem();
			connectionHandler.sendPacket(placePacket);
			fishing = true;
		}
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
		BlockLocation ourLocation = new BlockLocation((int) (Math.round(player
				.getX() - 0.5)), (int) player.getY(), (int) (Math.round(player
				.getZ() - 0.5)));
		int radius = 8;
		int closestX = 0, closestY = 0, closestZ = 0, closestDistance = Integer.MAX_VALUE;
		for(int x = -radius; x < radius; x++) {
			for(int y = -radius; y < radius; y++) {
				for(int z = -radius; z < radius; z++) {
					int id = world.getBlockIdAt(ourLocation.getX() + x,
							ourLocation.getY() + y, ourLocation.getZ() + z);
					int distance = player.getDistanceToSquared(
							ourLocation.getX() + x, ourLocation.getY() + y,
							ourLocation.getZ() + z);
					if((id == 8 || id == 9) && distance < closestDistance) {
						closestX = ourLocation.getX() + x;
						closestY = ourLocation.getY() + y;
						closestZ = ourLocation.getZ() + z;
						closestDistance = distance;
					}
				}
			}
		}
		return closestDistance < Integer.MAX_VALUE ? new BlockLocation(
				closestX, closestY, closestZ) : null;
	}

	@EventHandler
	public void onPacketProcess(PacketProcessEvent event) {
		Packet packet = event.getPacket();
		if(packet instanceof Packet28EntityVelocity) {
			Packet28EntityVelocity velocityPacket = (Packet28EntityVelocity) packet;
			World world = bot.getWorld();
			if(world == null)
				return;
			Entity entity = world.getEntityById(velocityPacket.entityId);
			if(entity == null || !(entity instanceof FishingBobEntity))
				return;
			System.out.println("Fishing bob velocity!");
			if(velocityPacket.motionX == 0 && velocityPacket.motionY < 0
					&& velocityPacket.motionZ == 0) {
				if(fishing) {
					if(!switchToFishingRod()) {
						stop();
						return;
					}
					MainPlayerEntity player = bot.getPlayer();
					ConnectionHandler connectionHandler = bot
							.getConnectionHandler();
					connectionHandler.sendPacket(new Packet18Animation(player
							.getId(), Animation.SWING_ARM));
					Packet15Place placePacket = new Packet15Place();
					placePacket.xPosition = -1;
					placePacket.yPosition = -1;
					placePacket.zPosition = -1;
					placePacket.direction = 255;
					placePacket.itemStack = player.getInventory()
							.getCurrentHeldItem();
					connectionHandler.sendPacket(placePacket);
				}
				fishing = false;
			}
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
