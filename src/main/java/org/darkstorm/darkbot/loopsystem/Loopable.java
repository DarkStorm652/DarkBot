package org.darkstorm.darkbot.loopsystem;

public interface Loopable {
	public static final int STOP = -1;
	public static final int YIELD = -2;
	public static final int WAIT = -3;

	public int loop() throws InterruptedException;
}
