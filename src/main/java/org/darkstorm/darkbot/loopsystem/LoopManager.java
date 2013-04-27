package org.darkstorm.darkbot.loopsystem;

import java.util.Vector;

public class LoopManager {
	private ThreadGroup threadGroup;
	private Vector<LoopHandler> loopHandlers;
	private LoopHandlerFactory loopHandlerFactory;
	private boolean started = false;
	private boolean paused = false;

	public LoopManager(ThreadGroup threadGroup) {
		this(threadGroup, true);
	}

	public LoopManager(ThreadGroup threadGroup, boolean autoStart) {
		if(threadGroup == null)
			throw new IllegalArgumentException(
					"param 0 (type ThreadGroup) is null");
		this.threadGroup = threadGroup;
		loopHandlers = new Vector<LoopHandler>();
		loopHandlerFactory = new LoopHandlerFactory(this);
		started = autoStart;
	}

	public LoopHandler addLoopable(Loopable loopable) {
		return addLoopable(loopable, Integer.toString(loopHandlers.size()));
	}

	public LoopHandler addLoopable(Loopable loopable, String name) {
		LoopHandler loopHandlerForLoop = loopHandlerFactory
				.produceLoopableController(loopable, name);
		loopHandlers.add(loopHandlerForLoop);
		return loopHandlerForLoop;
	}

	public LoopHandler removeLoopable(Loopable loopable) {
		if(loopable == null)
			throw new IllegalArgumentException("param 0 (type Loop) is null");
		for(LoopHandler loopHandler : loopHandlers) {
			Loopable loopableControllerLoop = loopHandler.getLoopable();
			if(loopableControllerLoop.equals(loopable)) {
				loopHandler.stop();
				loopHandlers.remove(loopHandler);
				return loopHandler;
			}
		}
		return null;
	}

	public void startAll() {
		for(LoopHandler loopHandler : loopHandlers)
			loopHandler.start();
		started = true;
	}

	public void stopAll() {
		for(LoopHandler loopHandler : loopHandlers)
			loopHandler.stop();
		started = false;
	}

	public void pauseAll() {
		for(LoopHandler loopHandler : loopHandlers)
			loopHandler.pause();
		paused = true;
	}

	public void resumeAll() {
		for(LoopHandler loopHandler : loopHandlers)
			loopHandler.pause();
		paused = false;
	}

	public boolean checkForLivingControllers() {
		for(LoopHandler loopHandler : loopHandlers)
			if(loopHandler.isAlive())
				return true;
		return false;
	}

	public boolean checkForActiveControllers() {
		for(LoopHandler loopHandler : loopHandlers)
			if(loopHandler.isActive())
				return true;
		return false;
	}

	public ThreadGroup getThreadGroup() {
		return threadGroup;
	}

	public LoopHandler[] getLoopHandlers() {
		return loopHandlers.toArray(new LoopHandler[0]);
	}

	public boolean hasStarted() {
		return started;
	}

	public boolean isPaused() {
		return paused;
	}
}
