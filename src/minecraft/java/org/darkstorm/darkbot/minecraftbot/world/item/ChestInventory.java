package org.darkstorm.darkbot.minecraftbot.world.item;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.events.EventHandler;
import org.darkstorm.darkbot.minecraftbot.events.io.PacketSentEvent;
import org.darkstorm.darkbot.minecraftbot.protocol.*;
import org.darkstorm.darkbot.minecraftbot.protocol.bidirectional.Packet101CloseWindow;
import org.darkstorm.darkbot.minecraftbot.protocol.writeable.Packet102WindowClick;

public class ChestInventory implements Inventory {
	private final MinecraftBot bot;
	private final ItemStack[] items;
	private final ItemStack[] inventory = new ItemStack[36];
	private final int id;

	private ItemStack selectedItem = null;

	public ChestInventory(MinecraftBot bot, int id, boolean large) {
		this.bot = bot;
		this.id = id;
		items = new ItemStack[large ? 54 : 27];
	}

	@EventHandler
	public synchronized void onPacketSent(PacketSentEvent event) {
		Packet packet = event.getPacket();
		if(packet instanceof Packet101CloseWindow
				&& ((Packet101CloseWindow) packet).windowId == 0) {
			selectedItem = null;
		}
	}

	@Override
	public synchronized int getSize() {
		return items.length;
	}

	@Override
	public synchronized ItemStack getItemAt(int slot) {
		return slot < items.length ? items[slot] : inventory[slot
				- items.length];
	}

	@Override
	public synchronized void setItemAt(int slot, ItemStack item) {
		System.out.println("Set chest item at " + slot + ": " + item);
		if(slot < items.length)
			items[slot] = item;
		else
			inventory[slot - items.length] = item;
	}

	@Override
	public void setItemFromServerAt(int serverSlot, ItemStack item) {
		setItemAt(serverSlot, item);
	}

	public synchronized void selectItemAt(int slot) {
		selectItemAt(slot, true);
	}

	@Override
	public synchronized void selectItemAt(int slot, boolean leftClick) {
		ConnectionHandler connectionHandler = bot.getConnectionHandler();
		ItemStack item = getItemAt(slot);
		ItemStack oldSelected = selectedItem;
		if(leftClick) {
			if(selectedItem != null) {
				if(item != null) {
					if(item.getId() == selectedItem.getId()) {
						if(item.getStackSize() != 64) {
							int newStackSize = item.getStackSize()
									+ selectedItem.getStackSize();
							item.setStackSize(Math.min(64, newStackSize));
							newStackSize -= 64;
							if(newStackSize > 0)
								selectedItem.setStackSize(newStackSize);
							else
								selectedItem = null;
						}
					} else {
						setItemAt(slot, selectedItem);
						selectedItem = item;
					}
				} else {
					setItemAt(slot, selectedItem);
					selectedItem = null;
				}
			} else if(item != null) {
				setItemAt(slot, null);
				selectedItem = item;
			}
		} else {
			if(selectedItem != null) {
				if(item != null) {
					if(item.getId() == selectedItem.getId()) {
						if(item.getStackSize() != 64) {
							item.setStackSize(item.getStackSize() + 1);
							if(selectedItem.getStackSize() > 1)
								selectedItem.setStackSize(selectedItem
										.getStackSize() - 1);
							else
								selectedItem = null;
						}
					} else {
						setItemAt(slot, selectedItem);
						selectedItem = item;
					}
				} else {
					ItemStack newItem = selectedItem.clone();
					newItem.setStackSize(1);
					setItemAt(slot, newItem);
					if(selectedItem.getStackSize() > 1)
						selectedItem
								.setStackSize(selectedItem.getStackSize() - 1);
					else
						selectedItem = null;
				}
			} else if(item != null) {
				if(item.getStackSize() == 1) {
					selectedItem = item;
					setItemAt(slot, null);
				} else {
					int stackSize = item.getStackSize();
					item.setStackSize(stackSize / 2);
					ItemStack newSelectedItem = item.clone();
					newSelectedItem.setStackSize(newSelectedItem.getStackSize()
							+ (stackSize % 2));
					selectedItem = newSelectedItem;
				}
			}
		}
		delay();
		System.out.println("Clicked at " + slot + " | left: " + leftClick
				+ " item: " + item + " selected: " + oldSelected);
		connectionHandler.sendPacket(new Packet102WindowClick(id, slot,
				leftClick ? 0 : 1, false, item, (short) 0));
	}

	public synchronized void selectArmorAt(int slot) {
		selectItemAt(slot + 36, true);
	}

	public synchronized void selectCraftingAt(int slot) {
		selectCraftingAt(slot, true);
	}

	public synchronized void selectCraftingAt(int slot, boolean leftClick) {
		selectItemAt(slot + 40, leftClick);
	}

	@Override
	public synchronized void selectItemAtWithShift(int slot) {
		ItemStack item = getItemAt(slot);
		int rangeStart, rangeEnd;
		if(item == null)
			return;
		if(slot < items.length) {
			rangeStart = items.length;
			rangeEnd = items.length + inventory.length;
		} else {
			rangeStart = 0;
			rangeEnd = items.length;
		}
		boolean slotFound = false;
		for(int i = rangeStart; i < rangeEnd; i++) {
			if(items[i] == null) {
				if(slot < items.length) {
					items[slot] = null;
					inventory[slot - items.length] = item;
				} else {
					items[i] = item;
					inventory[slot - items.length] = null;
				}
				slotFound = true;
				break;
			}
		}
		if(!slotFound)
			return;
		ConnectionHandler connectionHandler = bot.getConnectionHandler();
		delay();
		connectionHandler.sendPacket(new Packet102WindowClick(id, slot, 0,
				true, item, (short) 0));
	}

	@Override
	public synchronized ItemStack getSelectedItem() {
		return selectedItem;
	}

	@Override
	public synchronized void dropSelectedItem() {
		selectedItem = null;
		ConnectionHandler connectionHandler = bot.getConnectionHandler();
		delay();
		connectionHandler.sendPacket(new Packet102WindowClick(id, -999, 0,
				true, null, (short) 0));
	}

	@Override
	public synchronized void close() {
		ConnectionHandler connectionHandler = bot.getConnectionHandler();
		connectionHandler.sendPacket(new Packet101CloseWindow(id));
	}

	private void delay() {
		int delay = bot.getPlayer().getInventory().getDelay();
		if(delay > 0) {
			try {
				Thread.sleep(delay);
			} catch(InterruptedException exception) {
				exception.printStackTrace();
			}
		}
	}

	public MinecraftBot getBot() {
		return bot;
	}

	@Override
	public int getWindowId() {
		return id;
	}
}
