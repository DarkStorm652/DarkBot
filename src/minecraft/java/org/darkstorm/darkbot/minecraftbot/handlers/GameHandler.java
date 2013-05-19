package org.darkstorm.darkbot.minecraftbot.handlers;

import java.util.*;
import java.util.concurrent.*;

import org.darkstorm.darkbot.minecraftbot.MinecraftBot;
import org.darkstorm.darkbot.minecraftbot.events.*;
import org.darkstorm.darkbot.minecraftbot.events.EventListener;
import org.darkstorm.darkbot.minecraftbot.events.general.DisconnectEvent;
import org.darkstorm.darkbot.minecraftbot.util.Timer;

public final class GameHandler extends MinecraftHandler implements
		EventListener {
	private final TickHandler tickHandler;
	private final List<GameListener> listeners = new ArrayList<GameListener>();

	public GameHandler(MinecraftBot bot) {
		super(bot);
		tickHandler = new TickHandler(this);
		bot.getEventManager().registerListener(this);
	}

	public void registerListener(GameListener listener) {
		if(listener == null)
			throw new NullPointerException();
		synchronized(listeners) {
			listeners.add(listener);
		}
	}

	public boolean unregisterListener(GameListener listener) {
		if(listener == null)
			throw new NullPointerException();
		synchronized(listeners) {
			return listeners.remove(listener);
		}
	}

	protected void runTick() {
		synchronized(listeners) {
			for(GameListener listener : listeners) {
				try {
					listener.onTick();
				} catch(Throwable exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	@EventHandler
	public void onDisconnect(DisconnectEvent event) {
		tickHandler.thread.cancel(true);
	}

	@Override
	public String getName() {
		return "GameHandler";
	}

	private final class TickHandler implements Runnable {
		private final Timer timer = new Timer(20, 20);
		private final Future<?> thread;

		public TickHandler(GameHandler gameHandler) {
			MinecraftBot bot = gameHandler.getBot();
			ExecutorService service = bot.getService();
			thread = service.submit(this);
		}

		@Override
		public void run() {
			while(true) {
				if(thread != null && thread.isCancelled())
					return;
				timer.update();
				for(int i = 0; i < timer.getElapsedTicks(); i++)
					runTick();
				if(timer.getFPSCoolDown() > 0) {
					try {
						Thread.sleep(timer.getFPSCoolDown());
					} catch(InterruptedException exception) {}
				}
			}
		}
	}
}
