package org.darkstorm.minecraft.darkbot.event.protocol.server;

import com.github.steveice10.mc.protocol.data.game.window.WindowType;

public class WindowOpenEvent extends WindowEvent {
	private final WindowType inventoryType;
	private final String windowTitle;
	private final int slotCount;

	public WindowOpenEvent(int windowId, WindowType inventoryType, String windowTitle, int slotCount) {
		super(windowId);

		this.inventoryType = inventoryType;
		this.windowTitle = windowTitle;
		this.slotCount = slotCount;
	}

	public WindowType getInventoryType() {
		return inventoryType;
	}

	public String getWindowTitle() {
		return windowTitle;
	}

	public int getSlotCount() {
		return slotCount;
	}
}
