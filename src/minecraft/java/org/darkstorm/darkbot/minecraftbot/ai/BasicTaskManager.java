package org.darkstorm.darkbot.minecraftbot.ai;

import java.util.*;

import java.math.BigInteger;

public class BasicTaskManager implements TaskManager {
	private final Map<Class<? extends Task>, Task> tasks = new HashMap<Class<? extends Task>, Task>();
	private final Map<Task, BigInteger> startTimes = new HashMap<Task, BigInteger>();

	public BasicTaskManager() {
	}

	@Override
	public synchronized boolean registerTask(Task task) {
		if(task == null)
			return false;
		if(tasks.get(task.getClass()) != null)
			return false;
		tasks.put(task.getClass(), task);
		return true;
	}

	@Override
	public synchronized boolean unregisterTask(Task task) {
		if(task == null)
			return false;
		return unregisterTask(task.getClass());
	}

	@Override
	public synchronized boolean unregisterTask(Class<? extends Task> taskClass) {
		if(taskClass == null)
			return false;
		Task task = tasks.remove(taskClass);
		if(task != null) {
			if(task.isActive())
				task.stop();
			startTimes.remove(task);
		}
		return task != null;
	}

	@Override
	public synchronized void update() {
		List<Task> exclusiveIgnoringTasks = new ArrayList<Task>();
		Task highestExclusiveTask = null;
		int highestPriority = -1;
		BigInteger highestStartTime = null;
		for(Task task : tasks.values()) {
			boolean active = task.isActive();
			boolean hasStartTime = startTimes.containsKey(task);
			if(hasStartTime && !active)
				startTimes.remove(task);
			else if(!hasStartTime && active)
				startTimes.put(task,
						BigInteger.valueOf(System.currentTimeMillis()));

			if(!active && task.isPreconditionMet()) {
				if(task.start())
					startTimes.put(task,
							BigInteger.valueOf(System.currentTimeMillis()));
				else
					task.stop();
			}
			if(task.isExclusive() && active) {
				int taskPriority = task.getPriority().ordinal();
				BigInteger taskStartTime = startTimes.get(task);
				if(highestExclusiveTask == null
						|| taskPriority > highestPriority
						|| (taskPriority == highestPriority && taskStartTime
								.compareTo(highestStartTime) < 0)) {
					highestExclusiveTask = task;
					highestPriority = taskPriority;
					highestStartTime = taskStartTime;
				}
			}
			if(task.ignoresExclusive())
				exclusiveIgnoringTasks.add(task);
		}

		if(highestExclusiveTask != null) {
			highestExclusiveTask.run();
			if(!highestExclusiveTask.isActive()) {
				highestExclusiveTask.stop();
				startTimes.remove(highestExclusiveTask);
			}
			for(Task task : exclusiveIgnoringTasks) {
				if(task.isActive()) {
					task.run();
					if(!task.isActive()) {
						task.stop();
						startTimes.remove(task);
					}
				}
			}
			return;
		}

		for(Task task : tasks.values()) {
			if(task.isActive()) {
				task.run();
				if(!task.isActive()) {
					task.stop();
					startTimes.remove(task);
				}
			}
		}
	}

	@Override
	public synchronized void stopAll() {
		for(Task task : tasks.values()) {
			if(task.isActive())
				task.stop();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public synchronized <T extends Task> T getTaskFor(Class<T> taskClass) {
		if(taskClass == null)
			return null;
		return (T) tasks.get(taskClass);
	}

	@Override
	public List<Task> getRegisteredTasks() {
		return new ArrayList<Task>(tasks.values());
	}

}
