package org.darkstorm.darkbot.mcspambot;

public interface Backend {
	public void enable();

	public void say(String message);

	public void disable();
}
