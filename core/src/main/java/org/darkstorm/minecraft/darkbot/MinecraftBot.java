package org.darkstorm.minecraft.darkbot;

import com.github.steveice10.packetlib.Session;
import org.darkstorm.minecraft.darkbot.ai.TaskManager;
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

    public Session getConnectionHandler();
	
	public MainPlayerEntity getPlayer();
	
	public boolean hasSpawned();

	public World getWorld();

	public EventBus getEventBus();

	public TaskManager getTaskManager();

	public boolean isConnected();

	public void disconnect(String string);
}
