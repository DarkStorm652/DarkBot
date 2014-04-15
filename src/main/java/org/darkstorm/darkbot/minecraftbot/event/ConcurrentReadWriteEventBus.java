package org.darkstorm.darkbot.minecraftbot.event;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.locks.*;

import org.darkstorm.darkbot.minecraftbot.event.MultiEventException.EventException;

public class ConcurrentReadWriteEventBus implements EventBus {
	private final Set<ConcurrentReadWriteEventDelegate<?>> delegates;
	private final Lock readLock, writeLock;

	public ConcurrentReadWriteEventBus() {
		delegates = new HashSet<ConcurrentReadWriteEventDelegate<?>>();

		// Fairness to decrease starvation (registry occurs near initialization, so initial throughput decrease is acceptable)
		ReadWriteLock lock = new ReentrantReadWriteLock(true);
		readLock = lock.readLock();
		writeLock = lock.writeLock();
	}

	@Override
	public void fire(Event event) {
		Class<?> eventClass = event.getClass();
		List<ConcurrentReadWriteEventDelegate<?>> targets = new ArrayList<>();

		readLock.lock();
		try {
			for(ConcurrentReadWriteEventDelegate<?> delegate : delegates)
				if(delegate.getEventClass().isAssignableFrom(eventClass))
					targets.add(delegate);
		} finally {
			readLock.unlock();
		}

		List<EventException> exceptions = new ArrayList<>();
		for(ConcurrentReadWriteEventDelegate<?> target : targets) {
			try {
				fireDelegated(target, event);
			} catch(MultiEventException exception) {
				for(EventException cause : exception.getExceptions())
					exceptions.add(cause);
			}
		}

		if(!exceptions.isEmpty())
			throw new MultiEventException("Exception occurred firing " + event.getName(), exceptions.toArray(new EventException[exceptions.size()]));
	}

	private <T extends Event> void fireDelegated(ConcurrentReadWriteEventDelegate<T> delegate, Event event) throws MultiEventException {
		delegate.handleEvent(delegate.getEventClass().cast(event));
	}

	@Override
	public void register(EventListener listener) {
		Class<? extends EventListener> listenerClass = listener.getClass();

		writeLock.lock();
		try {
			for(Method method : listenerClass.getDeclaredMethods()) {
				if(method.getAnnotation(EventHandler.class) == null)
					continue;
				if(!method.isAccessible())
					method.setAccessible(true);
				if(method.getParameterTypes().length != 1)
					throw new IllegalArgumentException(String.format(	"Method %s in class %s has incorrect amount of parameters",
																		method,
																		listenerClass.getName()));
				Class<? extends Event> eventClass = method.getParameterTypes()[0].asSubclass(Event.class);
				boolean hasDelegate = false;
				for(ConcurrentReadWriteEventDelegate<?> delegate : delegates) {
					Class<?> delegateEventClass = delegate.getEventClass();
					if(delegateEventClass.isAssignableFrom(eventClass))
						delegate.registerHandler(listener, method);
					if(eventClass.equals(delegateEventClass))
						hasDelegate = true;
				}
				if(!hasDelegate) {
					@SuppressWarnings({ "rawtypes", "unchecked" })
					ConcurrentReadWriteEventDelegate<?> delegate = new ConcurrentReadWriteEventDelegate(eventClass);
					delegates.add(delegate);
					delegate.registerHandler(listener, method);
				}
			}
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public void unregister(EventListener listener) {
		Class<? extends EventListener> listenerClass = listener.getClass();

		writeLock.lock();
		try {
			for(Method method : listenerClass.getMethods()) {
				if(method.getAnnotation(EventHandler.class) == null)
					continue;
				if(!method.isAccessible())
					method.setAccessible(true);
				if(method.getParameterTypes().length != 1)
					throw new IllegalArgumentException(String.format(	"Method %s in class %s has incorrect amount of parameters",
																		method,
																		listenerClass.getName()));
				Class<? extends Event> eventClass = method.getParameterTypes()[0].asSubclass(Event.class);
				for(Iterator<ConcurrentReadWriteEventDelegate<?>> iterator = delegates.iterator(); iterator.hasNext();) {
					ConcurrentReadWriteEventDelegate<?> delegate = iterator.next();
					Class<?> delegateEventClass = delegate.getEventClass();
					if(eventClass.isAssignableFrom(delegateEventClass)) {
						delegate.unregisterHandler(method);
						if(!delegate.hasHandlers())
							iterator.remove();
					}
				}
			}
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public void clearListeners() {
		writeLock.lock();
		try {
			for(ConcurrentReadWriteEventDelegate<?> delegate : delegates)
				delegate.clearHandlers();
			delegates.clear();
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public EventListener[] getListeners(Class<?> eventClass) {
		readLock.lock();
		try {
			List<EventListener> listeners = new ArrayList<>();
			for(ConcurrentReadWriteEventDelegate<?> delegate : delegates)
				if(delegate.getClass().isAssignableFrom(eventClass))
					listeners.addAll(delegate.getHandlers().keySet());
			return listeners.toArray(new EventListener[listeners.size()]);
		} finally {
			readLock.unlock();
		}
	}
}
