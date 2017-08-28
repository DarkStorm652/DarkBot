package org.darkstorm.minecraft.darkbot.world.item;

import com.github.steveice10.mc.protocol.data.game.window.ClickItemParam;
import com.github.steveice10.mc.protocol.data.game.window.DropItemParam;
import com.github.steveice10.mc.protocol.data.game.window.ShiftClickItemParam;
import com.github.steveice10.mc.protocol.data.game.window.WindowAction;
import org.darkstorm.minecraft.darkbot.MinecraftBot;
import org.darkstorm.minecraft.darkbot.event.EventHandler;
import org.darkstorm.minecraft.darkbot.event.protocol.client.*;
import org.darkstorm.minecraft.darkbot.event.protocol.server.WindowCloseEvent;
import org.darkstorm.minecraft.darkbot.event.protocol.server.WindowTransactionCompleteEvent;

public class GenericInventory extends AbstractInventory {
	private final ItemStack[] items;
	private final ItemStack[] inventory = new ItemStack[36];

	private ItemStack selectedItem = null;

	public GenericInventory(MinecraftBot bot, int id, int size) {
		super(bot, id);
		items = new ItemStack[size];
	}

	@EventHandler
	public synchronized void onWindowClose(WindowCloseEvent event) {
		if(getWindowId() == event.getWindowId())
			selectedItem = null;
	}

	@EventHandler
	public synchronized void onWindowTransactionComplete(WindowTransactionCompleteEvent event) {
		if(!event.isAccepted())
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
		System.out.println("Set inventory item at " + slot + ": " + item);
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

		System.out.println("Clicked at " + slot + " | left: " + leftClick + " item: " + item);
		perform(new InventoryChangeEvent(this, slot,
				WindowAction.CLICK_ITEM, (short) 1, item, leftClick ? ClickItemParam.LEFT_CLICK : ClickItemParam.RIGHT_CLICK));
		selectedItem = item;
	}

	@Override
	public synchronized void selectItemAtWithShift(int slot) {
		ItemStack item = getItemAt(slot);

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
