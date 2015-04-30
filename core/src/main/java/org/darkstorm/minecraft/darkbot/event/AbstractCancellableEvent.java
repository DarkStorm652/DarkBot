package org.darkstorm.darkbot.minecraftbot.event;

public abstract class AbstractCancellableEvent extends AbstractEvent implements CancellableEvent {
	private boolean cancelled = false;

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
