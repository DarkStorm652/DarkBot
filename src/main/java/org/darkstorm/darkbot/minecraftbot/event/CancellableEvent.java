package org.darkstorm.darkbot.minecraftbot.event;

public interface CancellableEvent extends Event {
	public boolean isCancelled();
	public void setCancelled(boolean cancelled);
}
