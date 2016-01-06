package org.darkstorm.minecraft.darkbot.event;

public interface EventBus {
	public void fire(Event event);
	public void fireWithError(Event event) throws MultiEventException, UnsupportedOperationException;
	public void fireAsync(Event event) throws UnsupportedOperationException;

	public void register(EventListener listener);
	public void unregister(EventListener listener);
	public void clearListeners();

	public EventListener[] getListeners(Class<?> eventClass);
}
