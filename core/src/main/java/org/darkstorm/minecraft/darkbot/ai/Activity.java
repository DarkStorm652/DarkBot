package org.darkstorm.minecraft.darkbot.ai;

public interface Activity {
	public void run();

	public boolean isActive();

	public void stop();
}
