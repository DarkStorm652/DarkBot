package org.darkstorm.darkbot.minecraftbot.ai;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.events.EventHandler;
import org.darkstorm.darkbot.minecraftbot.events.io.PacketProcessEvent;
import org.darkstorm.darkbot.minecraftbot.handlers.ConnectionHandler;
import org.darkstorm.darkbot.minecraftbot.protocol.Packet;
import org.darkstorm.darkbot.minecraftbot.protocol.writeable.*;
import org.darkstorm.darkbot.minecraftbot.world.entity.MainPlayerEntity;
import org.darkstorm.darkbot.minecraftbot.world.item.*;

public class EatTask implements Task {
	private static final int[] FOOD_LIST;

	static {
		FOOD_LIST = new int[] { 260, 282, 297, 319, 320, 322, 349, 350, 357,
				360, 363, 364, 365, 366, 367 };
	}

	private final MinecraftBot bot;

	private boolean active;

	private int lastHealth, lastHunger, eatingTicks, lastSlot;

	public EatTask(MinecraftBot bot) {
		this.bot = bot;
	}

	@Override
	public boolean isPreconditionMet() {
		MainPlayerEntity player = bot.getPlayer();
		if(player == null)
			return false;
		PlayerInventory inventory = player.getInventory();
		if(lastHealth != player.getHealth() && player.getHealth() == 20)
			lastHealth = 20;
		if(lastHunger != player.getHunger() && player.getHunger() == 20)
			lastHunger = 20;
		if(player != null
				&& (player.getHunger() < 20 && player.getHunger() != lastHunger))
			for(int i = 0; i < FOOD_LIST.length; i++)
				if(inventory.contains(FOOD_LIST[i]))
					return true;
		return false;
	}

	@Override
	public boolean start(String... options) {
		if(isPreconditionMet()) {
			active = true;
			return true;
		} else
			return false;
	}

	@Override
	public void stop() {
		active = false;
	}

	@Override
	public void run() {
		MainPlayerEntity player = bot.getPlayer();
		ConnectionHandler connectionHandler = bot.getConnectionHandler();
		PlayerInventory inventory = player.getInventory();
		if(eatingTicks > 0) {
			eatingTicks--;
			if(eatingTicks == 0) {
				if(inventory.getCurrentHeldSlot() != lastSlot)
					inventory.setCurrentHeldSlot(lastSlot);
				stop();
			}
			return;
		}
		for(int i = 0; i < 36; i++) {
			ItemStack item = inventory.getItemAt(i);
			if(item == null || !isFood(item.getId()))
				continue;
			if(i > 8) {
				int hotbarSpace = -1;
				for(int hotbarIndex = 0; hotbarIndex < 9; hotbarIndex++) {
					if(inventory.getItemAt(hotbarIndex) == null) {
						hotbarSpace = hotbarIndex;
						break;
					}
				}
				if(hotbarSpace == -1) {
					stop();
					break;
				}
				inventory.selectItemAt(i);
				inventory.selectItemAt(hotbarSpace);
				inventory.close();
				i = hotbarSpace;
			}
			int lastHeldSlot = inventory.getCurrentHeldSlot();
			if(i != lastHeldSlot)
				inventory.setCurrentHeldSlot(i);
			Packet15Place placePacket = new Packet15Place();
			placePacket.xPosition = -1;
			placePacket.yPosition = -1;
			placePacket.zPosition = -1;
			placePacket.direction = -1;
			placePacket.itemStack = item;
			connectionHandler.sendPacket(placePacket);
			eatingTicks = 32;
			break;
		}
		lastHealth = player.getHealth();
		lastHunger = player.getHunger();
	}

	@EventHandler
	public void onPacketProcess(PacketProcessEvent event) {
		Packet packet = event.getPacket();
		if(packet instanceof Packet14BlockDig) {
			Packet14BlockDig digPacket = (Packet14BlockDig) packet;
			if(digPacket.status != 5)
				return;
			if(eatingTicks > 0) {
				MainPlayerEntity player = bot.getPlayer();
				PlayerInventory inventory = player.getInventory();
				if(inventory.getCurrentHeldSlot() != lastSlot)
					inventory.setCurrentHeldSlot(lastSlot);
				eatingTicks = 0;
			}
			if(active)
				stop();
		}
	}

	private boolean isFood(int id) {
		for(int i = 0; i < FOOD_LIST.length; i++)
			if(FOOD_LIST[i] == id)
				return true;
		return false;
	}

	@Override
	public boolean isActive() {
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
		return true;
	}

	@Override
	public String getName() {
		return "Eat";
	}

	@Override
	public String getOptionDescription() {
		return "";
	}
}
