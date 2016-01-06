package org.darkstorm.minecraft.darkbot.ai;

import java.util.List;

public interface TaskManager {
	public Activity getActivity();
	
	public void setActivity(Activity activity);
	
	public boolean hasActivity();
	
	public boolean registerTask(Task task);

	public boolean unregisterTask(Task task);

	public boolean unregisterTask(Class<? extends Task> task);

	public void update();

	public void stopAll();

	public <T extends Task> T getTaskFor(Class<T> taskClass);

	public List<Task> getRegisteredTasks();
}
