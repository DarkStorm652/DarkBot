package org.darkstorm.darkbot.loopsystem;

import static org.darkstorm.darkbot.loopsystem.Loopable.*;

import java.lang.Thread.UncaughtExceptionHandler;

public class LoopHandler implements UncaughtExceptionHandler {
	private LoopHandlerFactory factory;
	private Thread loopThread;
	private String name;
	private Loopable loopable;

	private boolean stop = false;
	private boolean pause = false;
	private boolean paused = false;

	public LoopHandler(LoopHandlerFactory factory, Loopable loopable,
			String name) {
		if(factory == null || loopable == null || name == null)
			throw new NullPointerException();
		this.factory = factory;
		this.loopable = loopable;
		this.name = name;
		createThread();
	}

	private void createThread() {
		ThreadGroup loopControllerThreadGroup = factory
				.getLoopControllerThreadGroup();
		Runnable loopThreadRunnable = createLoopThreadRunnable();
		String threadName = "LoopHandler [" + name + "]";
		loopThread = new Thread(loopControllerThreadGroup, loopThreadRunnable,
				threadName);
		setUncaughtExceptionHandler();
	}

	private Runnable createLoopThreadRunnable() {
		return new Runnable() {
			@Override
			public void run() {
				loopUntilStop();
			}
		};
	}

	private void loopUntilStop() {
		while(!stop) {
			loopUntilPaused();
			if(stop)
				break;
			setPausedIfOpposite(true);
			waitUntilInterrupted();
		}
		stop = false;
	}

	private void loopUntilPaused() {
		while(!pause) {
			setPausedIfOpposite(false);
			loopAndSleepWithExceptionCheck();
			if(stop)
				break;
		}
	}

	public void setDaemon(boolean daemon) {
		loopThread.setDaemon(daemon);
	}

	public boolean isDaemon() {
		return loopThread.isDaemon();
	}

	private void setPausedIfOpposite(boolean targetValueForPaused) {
		if(paused != targetValueForPaused)
			paused = targetValueForPaused;
	}

	private void loopAndSleepWithExceptionCheck() {
		try {
			loopAndSleep();
		} catch(RuntimeException e) {
			stop = false;
			throw e;
		}
	}

	private void loopAndSleep() {
		try {
			int sleepTime = loopable.loop();
			if(sleepTime < 0)
				handleLoopReturnCode(sleepTime);
			else
				Thread.sleep(sleepTime);
		} catch(InterruptedException e) {}
	}

	private void handleLoopReturnCode(int returnCode)
			throws InterruptedException {
		if(returnCode == STOP)
			stop();
		else if(returnCode == YIELD)
			Thread.yield();
		else if(returnCode == WAIT)
			wait();
	}

	private void waitUntilInterrupted() {
		try {
			wait();
		} catch(InterruptedException e) {}
	}

	private void setUncaughtExceptionHandler() {
		loopThread.setUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable e) {
		if(thread.equals(loopThread) && !(e instanceof ThreadDeath)) {
			System.err.print("Exception in thread \"" + thread.getName()
					+ "\": ");
			e.printStackTrace();
			System.err.println("The thread will now stop.");
		}
	}

	public synchronized void start() {
		if(!loopThread.isAlive()) {
			if(stop)
				stop = false;
			loopThread.start();
		}
	}

	public synchronized void stop() {
		if(!stop && loopThread.isAlive()) {
			stop = true;
			loopThread.interrupt();
		}
	}

	public synchronized void pause() {
		if(!pause && loopThread.isAlive()) {
			pause = true;
			loopThread.interrupt();
		}
	}

	public synchronized void resume() {
		if(pause && isAlive()) {
			pause = false;
			loopThread.interrupt();
		}
	}

	public synchronized boolean isAlive() {
		return loopThread.isAlive();
	}

	public synchronized boolean isActive() {
		return !paused;
	}

	public LoopHandlerFactory getFactory() {
		return factory;
	}

	public Loopable getLoopable() {
		return loopable;
	}

}
