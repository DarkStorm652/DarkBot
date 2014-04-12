package org.darkstorm.darkbot.minecraftbot.event;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.locks.*;

import org.darkstorm.darkbot.minecraftbot.event.MultiEventException.EventException;

final class ConcurrentReadWriteEventDelegate<T extends Event> {
	private final Class<T> eventClass;

	private final Map<EventHandler, EventHandlerData> eventHandlers;

	private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
	private final Lock readLock = lock.readLock(), writeLock = lock.writeLock();

	public ConcurrentReadWriteEventDelegate(Class<T> eventClass) {
		this.eventClass = eventClass;
		eventHandlers = new TreeMap<>(new EventHandlerPriorityComparator());
	}

	public void handleEvent(T event) throws MultiEventException {
		boolean cancelled = CancellableEvent.class.isAssignableFrom(event.getClass()) ? ((CancellableEvent) event).isCancelled() : false;
		List<EventHandlerData> targets = new ArrayList<>();
		List<EventException> exceptions = new ArrayList<>();

		readLock.lock();
		try {
			for(EventHandlerData data : eventHandlers.values()) {
				try {
					if(cancelled && data.handler.ignoreCancelled())
						continue;
					if(!data.method.isAccessible())
						data.method.setAccessible(true);
					targets.add(data);
				} catch(Throwable exception) {
					String message = "Exception occurred firing " + event.getName() + " in handler " + data.method.toString();
					exceptions.add(new EventException(event, message, exception, data.listener, data.method));
				}
			}
		} finally {
			readLock.unlock();
		}

		for(EventHandlerData target : targets) {
			try {
				target.method.invoke(target.listener, event);
			} catch(Throwable exception) {
				String message = "Exception occurred firing " + event.getName() + " in handler " + target.method.toString();
				exceptions.add(new EventException(event, message, exception, target.listener, target.method));
			}
		}

		if(!exceptions.isEmpty())
			throw new MultiEventException("Exception occurred firing " + event.getName(), exceptions.toArray(new EventException[exceptions.size()]));
	}

	public void registerHandler(EventListener listener, Method method) {
		EventHandler handler = method.getAnnotation(EventHandler.class);
		if(handler == null)
			return;

		writeLock.lock();
		try {
			eventHandlers.put(handler, new EventHandlerData(handler, listener, method));
		} finally {
			writeLock.unlock();
		}
	}

	public void unregisterHandler(Method method) {
		EventHandler handler = method.getAnnotation(EventHandler.class);
		if(handler == null)
			return;

		writeLock.lock();
		try {
			eventHandlers.remove(handler);
		} finally {
			writeLock.unlock();
		}
	}

	public Map<EventListener, Collection<Method>> getHandlers() {
		readLock.lock();
		try {
			Map<EventListener, Collection<Method>> handlers = new HashMap<>();
			for(EventHandlerData data : eventHandlers.values()) {
				Collection<Method> methods = handlers.get(data.listener);
				if(methods == null) {
					methods = new ArrayList<>();
					handlers.put(data.listener, methods);
				}
				methods.add(data.method);
			}
			return handlers;
		} finally {
			readLock.unlock();
		}
	}

	public boolean hasHandlers() {
		readLock.lock();
		try {
			return !eventHandlers.isEmpty();
		} finally {
			readLock.unlock();
		}
	}

	public void clearHandlers() {
		writeLock.lock();
		try {
			eventHandlers.clear();
		} finally {
			writeLock.unlock();
		}
	}

	public Class<T> getEventClass() {
		return eventClass;
	}

	private final class EventHandlerPriorityComparator implements Comparator<EventHandler> {
		@Override
		public int compare(EventHandler o1, EventHandler o2) {
			return Double.compare(o1.priority(), o2.priority());
		}
	}

	private final class EventHandlerData {
		private final EventHandler handler;
		private final EventListener listener;
		private final Method method;

		public EventHandlerData(EventHandler handler, EventListener listener, Method method) {
			this.handler = handler;
			this.listener = listener;
			this.method = method;
		}
	}
}
