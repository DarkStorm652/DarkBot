package org.darkstorm.minecraft.darkbot.event.protocol.client;

import com.github.steveice10.mc.protocol.data.game.window.WindowAction;
import com.github.steveice10.mc.protocol.data.game.window.WindowActionParam;
import org.darkstorm.minecraft.darkbot.world.item.*;

public class InventoryChangeEvent extends InventoryEvent {
	private final int slot;
	private final WindowAction windowAction;
	private final short transactionId;
	private final ItemStack item;
	private final WindowActionParam windowActionParam;

	public InventoryChangeEvent(Inventory inventory, int slot, WindowAction windowAction, short transactionId, ItemStack item, WindowActionParam windowActionParam) {
		super(inventory);

		this.slot = slot;
		this.windowAction = windowAction;
		this.transactionId = transactionId;
		this.item = item;
		this.windowActionParam = windowActionParam;
	}

	public int getSlot() {
		return slot;
	}

	public WindowAction getWindowAction() {
		return windowAction;
	}

	public short getTransactionId() {
		return transactionId;
	}

	public ItemStack getItem() {
		return item;
	}

	public WindowActionParam getWindowActionParam() {
		return windowActionParam;
	}
}
