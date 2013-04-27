package org.darkstorm.darkbot.loopsystem;

public class LoopHandlerFactory {
	private LoopManager loopManager;
	private ThreadGroup loopControllerThreadGroup;

	public LoopHandlerFactory(LoopManager loopManager) {
		if(loopManager == null)
			throw new IllegalArgumentException(
					"param 0 (type LoopHandler) is null");
		this.loopManager = loopManager;
		loopControllerThreadGroup = new ThreadGroup(loopManager
				.getThreadGroup(), "LoopControllers");
	}

	public LoopHandler produceLoopableController(Loopable loopable, String name) {
		LoopHandler loopHandler = new LoopHandler(this, loopable, name);
		if(loopManager.hasStarted()) {
			loopHandler.start();
			if(loopManager.isPaused())
				loopHandler.pause();
			else
				loopHandler.resume();
		} else
			loopHandler.stop();
		return loopHandler;
	}

	public ThreadGroup getLoopControllerThreadGroup() {
		return loopControllerThreadGroup;
	}

	public LoopManager getLoopManager() {
		return loopManager;
	}

}
