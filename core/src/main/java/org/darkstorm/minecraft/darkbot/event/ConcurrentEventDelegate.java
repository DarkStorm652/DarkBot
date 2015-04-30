package org.darkstorm.minecraft.darkbot.event;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.locks.*;

import org.darkstorm.minecraft.darkbot.event.MultiEventException.EventException;

final class ConcurrentEventDelegate<T extends Event> {
	private final Class<T> eventClass;

	private final Set<EventHandlerData> eventHandlers;

	private final ReadWriteLock lock = new ReentrantReadWriteLock(true);
	private final Lock readLock = lock.readLock(), writeLock = lock.writeLock();

	public ConcurrentEventDelegate(Class<T> eventClass) {
		this.eventClass = eventClass;

		eventHandlers = new TreeSet<>(new EventHandlerPriorityComparator());
	}

	public void handleEvent(T event) throws MultiEventException {
		boolean cancelled = CancellableEvent.class.isAssignableFrom(event.getClass()) ? ((CancellableEvent) event).isCancelled() : false;
		List<EventHandlerData> targets = new ArrayList<>();
		List<EventException> exceptions = new ArrayList<>();

		readLock.lock();
		try {
			for(EventHandlerData data : eventHandlers) {
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
		EventHandlerData data = new EventHandlerData(handler, listener, method, System.currentTimeMillis());

		writeLock.lock();
		try {
			eventHandlers.add(data);
		} finally {
			writeLock.unlock();
		}
	}

	public void unregisterHandler(EventListener listener, Method method) {
		EventHandler handler = method.getAnnotation(EventHandler.class);
		if(handler == null)
			return;
		EventHandlerData data = new EventHandlerData(handler, listener, method, System.currentTimeMillis());

		writeLock.lock();
		try {
			eventHandlers.remove(data);
		} finally {
			writeLock.unlock();
		}
	}

	public Map<EventListener, Collection<Method>> getHandlers() {
		readLock.lock();
		try {
			Map<EventListener, Collection<Method>> handlers = new HashMap<>();
			for(EventHandlerData data : eventHandlers) {
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

	private final class EventHandlerPriorityComparator implements Comparator<EventHandlerData> {
		@Override
		public int compare(EventHandlerData data1, EventHandlerData data2) {
			if(data1.equals(data2))
				return 0;
			int compare = -Double.compare(data1.handler.priority(), data2.handler.priority());
			if(compare == 0)
				compare = Long.compare(data1.registrationTime, data2.registrationTime);
			return compare != 0 ? compare : -1;
		}
	}

	private static final class EventHandlerData implements Cloneable {
		private final EventHandler handler;
		private final EventListener listener;
		private final Method method;
		private final long registrationTime;

		public EventHandlerData(EventHandler handler, EventListener listener, Method method, long registrationTime) {
			this.handler = handler;
			this.listener = listener;
			this.method = method;
			this.registrationTime = registrationTime;
		}

		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof EventHandlerData))
				return false;
			EventHandlerData other = (EventHandlerData) obj;
			return Objects.equals(listener, other.listener) && Objects.equals(method, other.method) && Objects.equals(handler, other.handler);
		}

		@Override
		public int hashCode() {
			return Objects.hash(listener, method, handler);
		}

		@Override
		public Object clone() {
			return new EventHandlerData(handler, listener, method, registrationTime);
		}
	}
}
