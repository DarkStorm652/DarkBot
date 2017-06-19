package org.darkstorm.minecraft.darkbot;

import org.darkstorm.minecraft.darkbot.ai.TaskManager;
import org.darkstorm.minecraft.darkbot.auth.AuthService;
import org.darkstorm.minecraft.darkbot.auth.Session;
import org.darkstorm.minecraft.darkbot.connection.*;
import org.darkstorm.minecraft.darkbot.event.EventBus;
import org.darkstorm.minecraft.darkbot.world.World;
import org.darkstorm.minecraft.darkbot.world.entity.MainPlayerEntity;

public interface MinecraftBot {
	public void sendChat(String message);

	public boolean isMuted();

	public void setMuted(boolean muted);

    public int getInventoryDelay();

    public void setInventoryDelay(int inventoryDelay);

    public boolean isMovementDisabled();

    public void setMovementDisabled(boolean movementDisabled);

    public ConnectionHandler getConnectionHandler();

    public ProxyData getLoginProxy();

    public ProxyData getConnectProxy();
	
	public MainPlayerEntity getPlayer();
	
	public boolean hasSpawned();

	public World getWorld();

	public Session getSession();

	public AuthService<?> getAuthService();

	public EventBus getEventBus();

	public TaskManager getTaskManager();

	public boolean isConnected();

	public void disconnect(String string);
}
