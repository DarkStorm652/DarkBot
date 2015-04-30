package org.darkstorm.minecraft.darkbot.world.item;

import java.util.*;

import org.darkstorm.minecraft.darkbot.MinecraftBot;
import org.darkstorm.minecraft.darkbot.event.*;
import org.darkstorm.minecraft.darkbot.event.EventListener;
import org.darkstorm.minecraft.darkbot.event.general.TickEvent;
import org.darkstorm.minecraft.darkbot.event.protocol.client.*;

public abstract class AbstractInventory implements Inventory, EventListener {
	private static class ActionHandler implements EventListener {
		private final MinecraftBot bot;
		
		private final Queue<InventoryEvent> queue = new ArrayDeque<>();
		private int cooldown;
		private boolean shutdown;
		
		public ActionHandler(MinecraftBot bot) {
			this.bot = bot;
			
			bot.getEventBus().register(this);
		}
		
		@EventHandler
		public void onTick(TickEvent event) {
			if(cooldown > 0) {
				cooldown--;
				return;
			}
			
			synchronized(queue) {
				if(shutdown && queue.isEmpty()) {
					bot.getEventBus().unregister(this);
					return;
				}
				
				while(cooldown <= 0 && !queue.isEmpty()) {
					performImmediately(queue.poll());
					cooldown += bot.getInventoryDelay();
				}
			}
		}
		
		public void perform(InventoryEvent action) {
			if(shutdown)
				return;
			
			if(bot.getInventoryDelay() > 0) {
				synchronized(queue) {
					queue.add(action);
				}
			} else
				performImmediately(action);
		}
		public void performImmediately(InventoryEvent action) {
			if(shutdown)
				return;
			
			bot.getEventBus().fire(action);
		}
		
		public boolean hasActionsPending() {
			synchronized(queue) {
				return !queue.isEmpty();
			}
		}
		
		public void shutdown() {
			shutdown = true;
		}
	}
	private static class GlobalActionHandler extends ActionHandler {
		public GlobalActionHandler(MinecraftBot bot) {
			super(bot);
		}
		
		@Override
		public void shutdown() {}
	}
	
	private static final Map<MinecraftBot, ActionHandler> actionHandlers = new WeakHashMap<>();

	protected final MinecraftBot bot;
	private final int id;
	
	private final ActionHandler actionHandler;
	
	public AbstractInventory(MinecraftBot bot, int id) {
		this(bot, id, true);
	}
	
	public AbstractInventory(MinecraftBot bot, int id, boolean actionsHandledGlobally) {
		this.bot = bot;
		this.id = id;
		
		if(actionsHandledGlobally) {
			synchronized(actionHandlers) {
				ActionHandler actionHandler = actionHandlers.get(bot);
				if(actionHandler == null)
					actionHandlers.put(bot, actionHandler = new GlobalActionHandler(bot));
				this.actionHandler = actionHandler;
			}
		} else
			actionHandler = new ActionHandler(bot);
		
		bot.getEventBus().register(this);
	}
	
	

	protected final void perform(InventoryEvent action) {
		actionHandler.perform(action);
	}
	protected final void performImmediately(InventoryEvent action) {
		actionHandler.performImmediately(action);
	}
	
	@Override
	public final boolean hasActionsQueued() {
		return actionHandler.hasActionsPending();
	}
	
	@Override
	public void close() {
		perform(new InventoryCloseEvent(this));
		destroy();
	}
	
	protected void destroy() {
		bot.getEventBus().unregister(this);
		actionHandler.shutdown();
	}
	
	@Override
	public int getWindowId() {
		return id;
	}
}
