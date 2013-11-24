package org.darkstorm.darkbot.mcwrapper.gui.spam;

import java.util.concurrent.*;

import org.darkstorm.darkbot.mcwrapper.gui.spam.ActionProvider.Action;
import org.darkstorm.darkbot.minecraftbot.MinecraftBot;

public class BasicActionManager implements ActionManager {
	private final MinecraftBot bot;
	private final ExecutorService service = Executors.newSingleThreadExecutor();

	private Action[] actions;
	private Future<?> thread;

	public BasicActionManager(MinecraftBot bot) {
		this.bot = bot;
	}

	@Override
	public synchronized void start() {
		if(thread != null && !thread.isDone())
			return;
		thread = service.submit(new ActionTask());
	}

	@Override
	public synchronized void stop() {
		if(thread == null || thread.isDone())
			return;
		thread.cancel(true);
	}

	@Override
	public synchronized void restart() {
		stop();
		thread = null;
		start();
	}

	@Override
	public synchronized boolean isRunning() {
		return thread != null && !thread.isDone();
	}

	@Override
	public synchronized Action[] getActions() {
		Action[] actions = new Action[this.actions.length];
		System.arraycopy(this.actions, 0, actions, 0, actions.length);
		return actions;
	}

	@Override
	public synchronized void setActions(Action... actions) {
		Action[] actionsCopy = new Action[actions.length];
		System.arraycopy(actions, 0, actionsCopy, 0, actionsCopy.length);
		this.actions = actionsCopy;
	}

	@Override
	public MinecraftBot getBot() {
		return bot;
	}

	private class ActionTask implements Runnable {
		private final Action[] actions;

		public ActionTask() {
			actions = getActions();
		}

		public void run() {
			for(Action action : actions)
				action.performAction(BasicActionManager.this);
		}
	}
}
