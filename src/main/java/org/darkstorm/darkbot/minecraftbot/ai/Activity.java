package org.darkstorm.darkbot.minecraftbot.ai;

public interface Activity {
	public void run();

	public boolean isActive();

	public void stop();
}
