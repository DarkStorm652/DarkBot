package org.darkstorm.darkbot.minecraftbot.event;

import java.lang.reflect.Method;

public final class MultiEventException extends Exception {
	private static final long serialVersionUID = -1392819062389929428L;

	private final EventException[] exceptions;

	public MultiEventException(EventException... exceptions) {
		this.exceptions = exceptions.clone();
	}

	public MultiEventException(String message, EventException... exceptions) {
		super(message);

		this.exceptions = exceptions.clone();
	}

	public EventException[] getExceptions() {
		return exceptions.clone();
	}

	public static final class EventException extends RuntimeException {
		private static final long serialVersionUID = -910511146985345487L;

		private final Event event;
		private final EventListener listener;
		private final Method method;

		public EventException(Event event, Throwable cause, EventListener listener, Method method) {
			super(cause);

			this.event = event;
			this.listener = listener;
			this.method = method;
		}

		public EventException(Event event, String message, Throwable cause, EventListener listener, Method method) {
			super(message, cause);

			this.event = event;
			this.listener = listener;
			this.method = method;
		}

		public Event getEvent() {
			return event;
		}

		public EventListener getListener() {
			return listener;
		}

		public Method getMethod() {
			return method;
		}
	}
}
