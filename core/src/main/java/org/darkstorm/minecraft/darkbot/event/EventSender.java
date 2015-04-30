package org.darkstorm.minecraft.darkbot.event;

import java.lang.reflect.*;
import java.util.*;

final class EventSender {
	private final Map<EventListener, List<Method>> handlers;
	private final Class<? extends Event> listenerEventClass;

	public EventSender(Class<? extends Event> listenerEventClass) {
		handlers = new HashMap<EventListener, List<Method>>();
		this.listenerEventClass = listenerEventClass;
	}

	public synchronized void addHandler(EventListener listener, Method method) {
		List<Method> methods = handlers.get(listener);
		if(methods == null) {
			methods = new ArrayList<>();
			handlers.put(listener, methods);
		}
		methods.add(method);
	}

	public synchronized void unregisterListener(EventListener listener) {
		handlers.remove(listener);
	}

	public synchronized List<EventListener> getListeners() {
		return Collections.unmodifiableList(new ArrayList<>(handlers.keySet()));
	}

	public synchronized void sendEvent(Event event) {
		Class<?> eventClass = event.getClass();
		if(!listenerEventClass.isAssignableFrom(eventClass))
			return;
		List<EventListener> listeners = new ArrayList<>(handlers.keySet());
		for(EventListener listener : listeners) {
			List<Method> methods = handlers.get(listener);
			if(methods == null)
				continue;
			for(Method method : methods) {
				try {
					boolean accessible = method.isAccessible();
					if(!accessible)
						method.setAccessible(true);
					method.invoke(listener, event);
					if(!accessible)
						method.setAccessible(false);
				} catch(InvocationTargetException exception) {
					exception.getCause().printStackTrace();
				} catch(Throwable exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	public Class<? extends Event> getListenerEventClass() {
		return listenerEventClass;
	}
}
