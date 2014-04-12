package org.darkstorm.darkbot.minecraftbot.event;

public interface EventBus {
	public void fire(Event event) throws MultiEventException;

	public void register(EventListener listener);
	public void unregister(EventListener listener);
	public void clearListeners();

	public EventListener[] getListeners(Class<?> eventClass);
}
