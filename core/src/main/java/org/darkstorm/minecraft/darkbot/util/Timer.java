package org.darkstorm.minecraft.darkbot.util;

public class Timer {
	private int frames;
	private int fps;
	private int maxFPS;
	private long fpsCoolDown;
	private long lastSecond;
	private long lastNanoSecond;
	private long ticksRun = 0;
	private float ticksPerSecond;
	private double lastHRTime;
	private int elapsedTicks;
	private float renderPartialTicks;
	private float timerSpeed;
	private float elapsedPartialTicks;
	private long lastSyncSysClock;
	private long lastSyncHRClock;
	private long field_28132_i;
	private double timeSyncAdjustment;

	public Timer(float ticksPerSecond, int maxFPS) {
		this.ticksPerSecond = ticksPerSecond;
		this.maxFPS = maxFPS;
		timerSpeed = 1.0F;
		elapsedPartialTicks = 0.0F;
		timeSyncAdjustment = 1.0D;
		lastSecond = System.currentTimeMillis();
		lastSyncSysClock = System.currentTimeMillis();
		lastSyncHRClock = System.nanoTime() / 0xf4240L;
	}

	public synchronized void update() {
		long currentTime = System.currentTimeMillis();
		long elapsedTime = currentTime - lastSyncSysClock;
		long l2 = System.nanoTime() / 0xf4240L;
		double d = l2 / 1000D;
		long elapsedTimeFromLastSecond = currentTime - lastSecond;
		long fpsCoolDown = ((lastNanoSecond + (0x3b9aca00 / (maxFPS / 2))) - System
				.nanoTime()) / 0xf4240L;
		if(fpsCoolDown > 0 && fpsCoolDown < 500)
			this.fpsCoolDown = fpsCoolDown;
		else
			this.fpsCoolDown = 0;
		if(elapsedTimeFromLastSecond >= 1000L) {
			fps = frames;
			frames = 0;
			lastSecond += 1000L;
		}
		if(elapsedTime > 1000L) {
			lastHRTime = d;
		} else if(elapsedTime < 0L) {
			lastHRTime = d;
		} else {
			field_28132_i += elapsedTime;
			if(field_28132_i > 1000L) {
				long l3 = l2 - lastSyncHRClock;
				double d2 = (double) field_28132_i / (double) l3;
				timeSyncAdjustment += (d2 - timeSyncAdjustment) * 0.20000000298023224D;
				lastSyncHRClock = l2;
				field_28132_i = 0L;
			}
			if(field_28132_i < 0L) {
				lastSyncHRClock = l2;
			}
		}
		lastSyncSysClock = currentTime;
		lastNanoSecond = System.nanoTime();
		frames++;
		double d1 = (d - lastHRTime) * timeSyncAdjustment;
		lastHRTime = d;
		if(d1 < 0.0D) {
			d1 = 0.0D;
		}
		if(d1 > 1.0D) {
			d1 = 1.0D;
		}
		elapsedPartialTicks += d1 * timerSpeed * ticksPerSecond;
		elapsedTicks = (int) elapsedPartialTicks;
		elapsedPartialTicks -= elapsedTicks;
		if(elapsedTicks > 10) {
			elapsedTicks = 10;
		}
		renderPartialTicks = elapsedPartialTicks;
		ticksRun++;
	}

	public long getTicksRun() {
		return ticksRun;
	}

	public int getFPS() {
		return fps;
	}

	public int getMaxFPS() {
		return maxFPS;
	}

	public void setMaxFPS(int maxFPS) {
		this.maxFPS = maxFPS;
	}

	public long getFPSCoolDown() {
		return fpsCoolDown;
	}

	public float getTicksPerSecond() {
		return ticksPerSecond;
	}

	public void setTicksPerSecond(float ticksPerSecond) {
		this.ticksPerSecond = ticksPerSecond;
	}

	public int getElapsedTicks() {
		return elapsedTicks;
	}

	public float getRenderPartialTicks() {
		return renderPartialTicks;
	}

	public float getTimerSpeed() {
		return timerSpeed;
	}

	public float getElapsedPartialTicks() {
		return elapsedPartialTicks;
	}
}
