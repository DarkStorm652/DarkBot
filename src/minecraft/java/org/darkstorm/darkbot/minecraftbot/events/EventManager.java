package org.darkstorm.darkbot.minecraftbot.events;

import java.util.*;

import java.lang.reflect.Method;

public final class EventManager {
	private final List<EventSender> eventSenders;

	public EventManager() {
		eventSenders = new ArrayList<EventSender>();
	}

	public synchronized void registerListener(EventListener listener) {
		Class<? extends EventListener> listenerClass = listener.getClass();
		for(Method method : listenerClass.getMethods()) {
			if(method.getAnnotation(EventHandler.class) == null)
				continue;
			if(!method.isAccessible())
				method.setAccessible(true);
			if(method.getParameterTypes().length != 1)
				throw new IllegalArgumentException("Method "
						+ method.toString() + " in class "
						+ listenerClass.getName()
						+ " has incorrect amount of parameters");
			Class<? extends Event> eventClass = method.getParameterTypes()[0]
					.asSubclass(Event.class);
			boolean senderExists = false;
			for(EventSender sender : eventSenders) {
				if(eventClass.isAssignableFrom(sender.getListenerEventClass())) {
					sender.addHandler(listener, method);
					senderExists = true;
				}
			}
			if(!senderExists) {
				EventSender sender = new EventSender(eventClass);
				eventSenders.add(sender);
				sender.addHandler(listener, method);
			}
		}
	}

	public synchronized void unregisterListener(EventListener listener) {
		for(EventSender sender : eventSenders) {
			sender.unregisterListener(listener);
		}
	}

	public synchronized void clearListeners() {
		eventSenders.clear();
	}

	public synchronized void sendEvent(Event event) {
		List<EventSender> sendTo = new ArrayList<EventSender>();
		for(EventSender sender : eventSenders) {
			Class<? extends Event> eventClass = sender.getListenerEventClass();
			if(eventClass.isInstance(event))
				sendTo.add(sender);
		}
		for(EventSender sender : sendTo)
			sender.sendEvent(event);
	}

	public synchronized List<EventListener> getListeners(
			Class<? extends Event> eventClass) {
		for(EventSender sender : eventSenders)
			if(eventClass.isAssignableFrom(sender.getListenerEventClass()))
				return sender.getListeners();
		return new ArrayList<EventListener>();
	}
}
