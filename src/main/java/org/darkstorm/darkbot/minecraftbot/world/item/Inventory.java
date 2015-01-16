package org.darkstorm.darkbot.minecraftbot.world.item;

public interface Inventory {
	public int getSize();

	public ItemStack getItemAt(int slot);

	public void setItemAt(int slot, ItemStack item);

	public void setItemFromServerAt(int serverSlot, ItemStack item);

	public void selectItemAt(int slot, boolean leftClick);

	public void selectItemAtWithShift(int slot);

	public ItemStack getSelectedItem();

	public void dropSelectedItem();

	public void close();

	public int getWindowId();
	
	public boolean hasActionsQueued();
}
