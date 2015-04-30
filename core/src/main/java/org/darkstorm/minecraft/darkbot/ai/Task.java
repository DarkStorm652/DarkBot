package org.darkstorm.minecraft.darkbot.ai;

public interface Task {
	/**
	 * Returns true if the precondition to activate it is met.
	 */
	public boolean isPreconditionMet();

	/**
	 * Starts the task.
	 */
	public boolean start(String... options);

	/**
	 * Stops the task. This is called either when it becomes inactive or it is
	 * told to stop.
	 */
	public void stop();

	/**
	 * Called every game tick that it is active.
	 */
	public void run();

	/**
	 * Returns true as long as the task can continue to run.
	 */
	public boolean isActive();

	/**
	 * Returns the priority of the task. This only pertains to tasks that are
	 * exclusive. If multiple exclusive tasks are active, the task with the
	 * highest priority will take precedence. If the there is more than one task
	 * of highest priority, the task that was started first will take
	 * precedence.
	 * 
	 * @see Task#isExclusive()
	 */
	public TaskPriority getPriority();

	/**
	 * Returns true if all other tasks should be put on hold while this task is
	 * active.
	 * 
	 * @see Task#getPriority()
	 */
	public boolean isExclusive();

	/**
	 * Returns true if this task ignores other active tasks that are exclusive.
	 */
	public boolean ignoresExclusive();

	/**
	 * The name of the task (e.g. FollowTask would have the name Follow)
	 */
	public String getName();

	/**
	 * Describes the options provided to start the task.
	 */
	public String getOptionDescription();
}
