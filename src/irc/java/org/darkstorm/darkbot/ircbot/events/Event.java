package org.darkstorm.darkbot.ircbot.events;

public abstract class Event {

	protected Object source;
	protected long when;
	protected Object argument;

	public Event(Object source, long when, Object argument) {
		this.source = source;
		this.when = when;
		this.argument = argument;
	}

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

	public long getWhen() {
		return when;
	}

	protected Object getArgumentAt(int index) {
		return ((Object[]) argument)[index];
	}
}
