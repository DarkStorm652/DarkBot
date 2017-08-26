package org.darkstorm.minecraft.darkbot.world.item;

import com.github.steveice10.mc.protocol.data.game.window.ClickItemParam;
import com.github.steveice10.mc.protocol.data.game.window.DropItemParam;
import com.github.steveice10.mc.protocol.data.game.window.ShiftClickItemParam;
import com.github.steveice10.mc.protocol.data.game.window.WindowAction;
import org.darkstorm.minecraft.darkbot.MinecraftBot;
import org.darkstorm.minecraft.darkbot.event.EventHandler;
import org.darkstorm.minecraft.darkbot.event.protocol.client.*;
import org.darkstorm.minecraft.darkbot.event.protocol.server.WindowCloseEvent;

public class ChestInventory extends AbstractInventory {
	private final ItemStack[] items;
	private final ItemStack[] inventory = new ItemStack[36];

	private ItemStack selectedItem = null;

	public ChestInventory(MinecraftBot bot, int id, boolean large) {
		super(bot, id);
		items = new ItemStack[large ? 54 : 27];
	}

	@EventHandler
	public synchronized void onWindowClose(WindowCloseEvent event) {
		if(getWindowId() == event.getWindowId())
			selectedItem = null;
	}

	@Override
	public synchronized int getSize() {
		return items.length;
	}

	@Override
	public synchronized ItemStack getItemAt(int slot) {
		return slot < items.length ? items[slot] : inventory[slot - items.length];
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
		ItemStack item = getItemAt(slot);
		ItemStack oldSelected = selectedItem;
		if(leftClick) {
			if(selectedItem != null) {
				if(item != null) {
					if(item.getId() == selectedItem.getId()) {
						if(item.getStackSize() != 64) {
							int newStackSize = item.getStackSize() + selectedItem.getStackSize();
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
								selectedItem.setStackSize(selectedItem.getStackSize() - 1);
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
						selectedItem.setStackSize(selectedItem.getStackSize() - 1);
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
					newSelectedItem.setStackSize(newSelectedItem.getStackSize() + (stackSize % 2));
					selectedItem = newSelectedItem;
				}
			}
		}
		System.out.println("Clicked at " + slot + " | left: " + leftClick + " item: " + item + " selected: " + oldSelected);
		perform(new InventoryChangeEvent(this, slot,
				WindowAction.CLICK_ITEM, (short) 1, item, ClickItemParam.LEFT_CLICK));

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
		perform(new InventoryChangeEvent(this, slot,
				WindowAction.SHIFT_CLICK_ITEM, (short) 1, item, ShiftClickItemParam.LEFT_CLICK));
	}

	@Override
	public synchronized ItemStack getSelectedItem() {
		return selectedItem;
	}

	@Override
	public synchronized void dropSelectedItem() {
		selectedItem = null;
		perform(new InventoryChangeEvent(this, -999,
				WindowAction.DROP_ITEM, (short) 1, null, DropItemParam.DROP_FROM_SELECTED));

	}
}
