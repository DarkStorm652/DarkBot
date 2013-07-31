package org.darkstorm.darkbot.minecraftbot.events;

import java.lang.reflect.Method;
import java.util.*;

public final class EventManager {
	private final List<EventSender> eventSenders;

	public EventManager() {
		eventSenders = new ArrayList<EventSender>();
	}

	public synchronized void registerListener(EventListener listener) {
		Class<? extends EventListener> listenerClass = listener.getClass();
		for(Method method : listenerClass.getMethods()) {
			if(!method.isAnnotationPresent(EventHandler.class))
				continue;
			if(method.getParameterTypes().length != 1)
				throw new IllegalArgumentException("Method " + method.toString() + " in class " + method.getDeclaringClass().getName() + " has incorrect amount of parameters");
			Class<? extends Event> eventClass = method.getParameterTypes()[0].asSubclass(Event.class);
			boolean senderExists = false;
			for(EventSender sender : eventSenders) {
				if(eventClass.isAssignableFrom(sender.getListenerEventClass()))
					sender.addHandler(listener, method);
				if(eventClass == sender.getListenerEventClass())
					senderExists = true;
			}
			if(!senderExists) {
				EventSender sender = new EventSender(eventClass);
				eventSenders.add(sender);
				sender.addHandler(listener, method);
			}
		}
	}

	public synchronized void unregisterListener(EventListener listener) {
		Iterator<EventSender> i = eventSenders.iterator();
		while(i.hasNext()) {
			EventSender sender = i.next();
			sender.unregisterListener(listener);
			if(sender.getListeners().isEmpty())
				i.remove();
		}
	}

	public synchronized void clearListeners() {
		eventSenders.clear();
	}

	public synchronized void sendEvent(Event event) {
		for(EventSender sender : new ArrayList<>(eventSenders)) {
			Class<? extends Event> eventClass = sender.getListenerEventClass();
			if(eventClass.isInstance(event))
				sender.sendEvent(event);
		}
	}

	public synchronized List<EventListener> getListeners(Class<? extends Event> eventClass) {
		for(EventSender sender : eventSenders)
			if(eventClass.isAssignableFrom(sender.getListenerEventClass()))
				return sender.getListeners();
		return Collections.emptyList();
	}
}
